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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    UserInfoUtils userInfoUtils;

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

        //判断是否有记录
        ShopFloorStatistics shopFloorStatistics = shopCenterService.findStatistics(homeShopCenter.getProvince(), homeShopCenter.getCity());
        if (shopFloorStatistics == null) {
            ShopFloorStatistics statistics = new ShopFloorStatistics();
            statistics.setTime(new Date());
            statistics.setDistributionState(0);
            statistics.setProvince(homeShopCenter.getProvince());
            statistics.setCity(homeShopCenter.getCity());
            statistics.setNumber(1);
            shopCenterService.addStatistics(statistics);
        } else {
            shopFloorStatistics.setTime(new Date());
            shopFloorStatistics.setDistributionState(0);
            shopFloorStatistics.setNumber(shopFloorStatistics.getNumber() + 1);
            shopCenterService.upStatistics(shopFloorStatistics);
        }
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
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
//        if (homeShopCenter.getShopState() == 1) {
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "恭喜您，已成功抢开到楼店。疫情过后，平台将会择时统一配货。感谢您的加入！", new JSONObject());
//        }
        //判断该用户是否实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(homeShopCenter.getUserId());
        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        //查询是否缴费
//        ShopFloor dishes = shopCenterService.findByUserId(CommonUtils.getMyId(), homeShopCenter.getVillageOnly());
//        if (dishes == null || dishes.getPayState() != 1) {
//            return returnData(StatusCode.CODE_BOND_NOT_AC.CODE_VALUE, "未缴纳保证金", new JSONObject());
//        }
        //查询用户钱包信息
        Map<String, Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO + CommonUtils.getMyId());
        if (purseMap == null || purseMap.size() <= 0) {
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE, "账户余额不足!", new JSONObject());
        }
        double spareMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if (spareMoney < 30000) {
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE, "账户余额不足!", new JSONObject());
        }
        shopCenterService.updateBusiness(homeShopCenter);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR + homeShopCenter.getUserId() + "_" + homeShopCenter.getVillageOnly(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
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
        shopFloorBondOrders.setMoney(30000);
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
     * 查询黑店列表(新)
     * @param time     时间：格式yyyy-MM-dd   默认null
     * @param shopState     店铺状态   -1不限 0未营业  1已营业
     * @param shopName     店铺名称 (默认null)
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findBlackSFList(@PathVariable String time, @PathVariable int shopState, @PathVariable String shopName, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        Date date = null;
        if (!CommonUtils.checkFull(time)) {
            time = time + " 00:00:00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = simpleDateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        PageBean<ShopFloor> pageBean = null;
        pageBean = shopCenterService.findNearbySFList2(date, province, city, district, shopState, shopName, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShopFloor ik = (ShopFloor) list.get(i);
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(ik.getUserId());
                if (userInfo != null) {
                    ik.setName(userInfo.getName());
                    ik.setHead(userInfo.getHead());
                    ik.setProTypeId(userInfo.getProType());
                    ik.setHouseNumber(userInfo.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询黑店数量（返回结构：总数、未配货的 、已配货的）
     * @param province     省(默认-1全部)
     * @param city      市(默认-1全部)
     * @param district    区(默认-1全部)
     * @return
     */
    @Override
    public ReturnData findSFnumList(@PathVariable int province, @PathVariable int city, @PathVariable int district) {
        int cont0 = 0;//全部
        int cont1 = 0;//今日新增
        int cont2 = 0;//开业店铺
        int cont3 = 0;//今日开业

        ShopFloor kh = null;
        List list = null;
        list = shopCenterService.findNum(province, city, district);//全部
        if (list != null && list.size() > 0) {
            cont0 = list.size();
            for (int i = 0; i < list.size(); i++) {
                kh = (ShopFloor) list.get(i);
                if (kh == null) {
                    continue;
                }
                if (kh.getDistributionTime() != null) {
                    boolean sign = false;
                    Date distributionTime = kh.getDistributionTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = simpleDateFormat.format(distributionTime);
                    try {
                        sign = isToday(date, "yyyy-MM-dd");//判断是不是当天
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (sign) {
                        if (kh.getDistributionState() == 1) {
                            cont3 += 1;
                        }
                        cont1 += 1;
                    }
                }
                if (kh.getDistributionState() == 1) {
                    cont2 += 1;
                }
            }
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("cont0", cont0);
        map.put("cont1", cont1);
        map.put("cont2", cont2);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    //判断日期是不是当天
    public static boolean isToday(String str, String formatStr) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            return false; //解析日期错误
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH) + 1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH) + 1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if (year1 == year2 && month1 == month2 && day1 == day2) {
            return true;
        }
        return false;
    }

    /***
     * 查询黑店列表(旧)
     * @param province     省 (经纬度>0时默认-1)
     * @param city      市 (经纬度>0时默认-1)
     * @param district    区 (经纬度>0时默认-1)
     * @param lat      纬度(省市区>0时默认-1)
     * @param lon      经度(省市区>0时默认-1)
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findNearbySFList(@PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<ShopFloor> pageBean = null;
        pageBean = shopCenterService.findNearbySFList(province, city, district, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShopFloor ik = (ShopFloor) list.get(i);
                if (province == -1 && lat > 0) {
                    double userlon = ik.getLon();
                    double userlat = ik.getLat();
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));
                    ik.setDistance(distance);//距离/m
                }
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(ik.getUserId());
                if (userInfo != null) {
                    ik.setName(userInfo.getName());
                    ik.setHead(userInfo.getHead());
                    ik.setProTypeId(userInfo.getProType());
                    ik.setHouseNumber(userInfo.getHouseNumber());
                }
            }
            if (province == -1 && lat > 0) {
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
     * 查询各地区黑店数量
     * @param shopState   店铺状态   -1不限 0未营业  1已营业
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findRegionSFlist(@PathVariable int shopState, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<ShopFloorStatistics> pageBean = null;
        pageBean = shopCenterService.findRegionSFlist(shopState, page, count);

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
