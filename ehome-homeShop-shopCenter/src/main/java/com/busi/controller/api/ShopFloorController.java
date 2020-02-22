package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ShopFloorService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 楼店信息相关接口 如：创建店铺 修改店铺信息 更改店铺状态等
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:23
 */
@RestController
public class ShopFloorController extends BaseController implements ShopFloorApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ShopFloorService shopCenterService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增店铺
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData addShopFloor(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有店铺
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOOR + CommonUtils.getMyId() + "_" + homeShopCenter.getVillageOnly());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ShopFloor kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId(), homeShopCenter.getVillageOnly());
            if (kitchen2 != null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增楼店失败，楼店已存在！", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增楼店失败，楼店已存在！", new JSONObject());
        }
        homeShopCenter.setAddTime(new Date());
        homeShopCenter.setUserId(CommonUtils.getMyId());
        shopCenterService.addHomeShop(homeShopCenter);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", homeShopCenter.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新店铺
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData changeShopFloor(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        shopCenterService.updateHomeShop(homeShopCenter);
        if (!CommonUtils.checkFull(homeShopCenter.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeShopCenter.getUserId(), homeShopCenter.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR + homeShopCenter.getUserId() + "_" + homeShopCenter.getVillageOnly(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData updShopFloorStatus(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
//        if (bindingResult.hasErrors()) {
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
//        }
//        //判断该用户是否实名
//        UserAccountSecurity userAccountSecurity = null;
//        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(homeShopCenter.getUserId());
//        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
//            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
//        }
//        //查询是否缴费
//        ShopFloor dishes = shopCenterService.findByUserId(CommonUtils.getMyId(), homeShopCenter.getVillageOnly());
//        if (dishes == null || dishes.getPayState() != 1) {
//            return returnData(StatusCode.CODE_BOND_NOT_AC.CODE_VALUE, "未缴纳保证金", new JSONObject());
//        }
//        shopCenterService.updateBusiness(homeShopCenter);
//        //清除缓存
//        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR + homeShopCenter.getUserId() + "_" + homeShopCenter.getVillageOnly(), 0);
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "恭喜您，已经抢开到楼店，在疫情过后，平台将会统一配货。", new JSONObject());
    }

    /***
     * 查询店铺信息
     * @param userId
     * @param villageOnly  小区唯一标识
     * @return
     */
    @Override
    public ReturnData findShopFloor(@PathVariable long userId, @PathVariable String villageOnly) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOOR + CommonUtils.getMyId() + "_" + villageOnly);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ShopFloor kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId(), villageOnly);
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOOR + kitchen2.getUserId() + "_" + villageOnly, kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        //判断该用户是否实名
        int realName = 0;//未实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(userId);
        if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) && !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            realName = 1;//已实名
        }
        kitchenMap.put("realName", realName);//未实名
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询店铺状态
     * @param userId
     * @return
     */
    @Override
    public ReturnData findFloorState(@PathVariable long userId, @PathVariable String villageOnly) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOOR + CommonUtils.getMyId() + "_" + villageOnly);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ShopFloor kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId(), villageOnly);
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOOR + kitchen2.getUserId() + "_" + villageOnly, kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ShopFloor ik = (ShopFloor) CommonUtils.mapToObject(kitchenMap, ShopFloor.class);
        Map<String, Object> map2 = new HashMap<>();
        if (ik == null) {
            map2.put("shopState", -1);//楼店不存在
        } else {
            map2.put("infoId", ik.getId());
            map2.put("shopState", ik.getShopState());  //-1楼店不存在  0未开店  1已开店
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 新增保证金订单
     * @param shopFloorBondOrders
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addBondOrder(@Valid @RequestBody ShopFloorBondOrders shopFloorBondOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        long time = new Date().getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + CommonUtils.getMyId() + random, 16);
        shopFloorBondOrders.setMoney(0.01);//初始30000
        shopFloorBondOrders.setOrderNumber(noRandom);
        shopFloorBondOrders.setTime(new Date());
        shopFloorBondOrders.setUserId(CommonUtils.getMyId());
        //放入缓存 5分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(shopFloorBondOrders);
        redisUtils.hmset(Constants.REDIS_KEY_BONDORDER + CommonUtils.getMyId() + "_" + shopFloorBondOrders.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_5);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", noRandom);
    }

    /***
     * 根据小区查询楼店
     * @param villageOnly
     * @return
     */
    @Override
    public ReturnData findVillage(@PathVariable String villageOnly) {
        if (CommonUtils.checkFull(villageOnly)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] array = villageOnly.split(",");
        List<ShopFloor> serverList;
        List<ShopFloor> newList = new ArrayList();
        serverList = shopCenterService.findByIds(array);
        for (int j = 0; j < array.length; j++) {
            ShopFloor floor = new ShopFloor();
            floor.setVillageOnly(array[j]);
            newList.add(floor);
        }
        if (serverList == null || serverList.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", newList);
        }
        for (int j = 0; j < newList.size(); j++) {
            ShopFloor shopFloor = newList.get(j);
            for (int i = 0; i < serverList.size(); i++) {
                ShopFloor serverShopFloor = serverList.get(i);
                if (serverShopFloor != null && shopFloor != null) {
                    if (serverShopFloor.getVillageOnly().equals(shopFloor.getVillageOnly())) {
                        newList.set(j, serverShopFloor);//替换掉原有对象
                        serverList.remove(i);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", newList);
    }

    /***
     * 查询附近楼店
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findNearbySFList(@PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<ShopFloor> pageBean = null;
        pageBean = shopCenterService.findNearbySFList(lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShopFloor ik = (ShopFloor) list.get(i);

//                double userlon = Double.valueOf(ik.getLon() + "");
//                double userlat = Double.valueOf(ik.getLat() + "");
                double userlon = ik.getLon();
                double userlat = ik.getLat();

                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

                ik.setDistance(distance);//距离/m
            }
            Collections.sort(list, new Comparator<ShopFloor>() {
                /*
                 * int compare(Person o1, Person o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和p2相等，
                 * 返回正数表示：o1大于o2
                 */
                @Override
                public int compare(ShopFloor o1, ShopFloor o2) {
                    // 按照距离进行正序排列
                    if (o1.getDistance() > o2.getDistance()) {
                        return 1;
                    }
                    if (o1.getDistance() == o2.getDistance()) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询用户楼店
     * @param userId   用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findUserSFlist(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<ShopFloor> pageBean = null;
        pageBean = shopCenterService.findUserSFlist(userId, page, count);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }


    /***
     * 新增永辉分类
     * @param yongHuiGoodsSort
     * @return
     */
    @Override
    public ReturnData addYHSort(@Valid @RequestBody YongHuiGoodsSort yongHuiGoodsSort, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        shopCenterService.addYHSort(yongHuiGoodsSort);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新永辉分类
     * @param yongHuiGoodsSort
     * @return
     */
    @Override
    public ReturnData changeYHSort(@Valid @RequestBody YongHuiGoodsSort yongHuiGoodsSort, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        shopCenterService.changeYHSort(yongHuiGoodsSort);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询永辉分类
     * @param levelOne 商品1级分类  默认为0, -2为不限
     * @param levelTwo 商品2级分类  默认为0, -2为不限
     * @param levelThree 商品3级分类  默认为0, -2为不限
     * @param letter 商品分类首字母  默认为null
     * @return
     */
    @Override
    public ReturnData findYHSort(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable String letter) {
        List sortList = null;
        sortList = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_SORTLIST + levelOne + "_" + levelTwo, 0, -1);
        if (sortList == null || sortList.size() <= 0) {
            sortList = shopCenterService.findYHSort(levelOne, levelTwo, levelThree, letter);
            if (sortList != null && sortList.size() > 0) {//防止没有三级的分类 出现异常
                //更新到缓存
                redisUtils.pushList(Constants.REDIS_KEY_SHOPFLOOR_SORTLIST + levelOne + "_" + levelTwo, sortList);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, sortList);
    }

    /**
     * @Description: 删除永辉分类
     * @return:
     */
    @Override
    public ReturnData delYHSort(@PathVariable String ids) {
        //验证参数
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        shopCenterService.delYHSort(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
