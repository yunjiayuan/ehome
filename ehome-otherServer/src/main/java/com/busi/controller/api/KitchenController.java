package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.KitchenService;
import com.busi.service.UserAccountSecurityService;
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
 * @description: 厨房
 * @author: ZHaoJiaJie
 * @create: 2019-03-01 16:35
 */
@RestController
public class KitchenController extends BaseController implements KitchenApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 新增厨房
     * @param kitchen
     * @return
     */
    @Override
    public ReturnData addKitchen(@Valid @RequestBody Kitchen kitchen, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有厨房
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Kitchen kitchen2 = kitchenService.findByUserId(kitchen.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        Kitchen ik = (Kitchen) CommonUtils.mapToObject(kitchenMap, Kitchen.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败，厨房不存在！", new JSONObject());
        }
        kitchen.setAuditType(1);
        kitchen.setBusinessStatus(1);//厨房默认关闭
        kitchen.setAddTime(new Date());
        //菜系最多选四个
        String[] cs = kitchen.getCuisine().split(",");
        if (cs.length >= 5) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "菜系最多选四个", new JSONObject());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(kitchen.getUserId());
        if (userInfo != null) {
            kitchen.setSex(userInfo.getSex());
            kitchen.setName(userInfo.getName());
            kitchen.setAge(getAge(userInfo.getBirthday()));//年龄
        }
        kitchenService.addKitchen(kitchen);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", kitchen.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 编辑厨房
     * @param kitchen
     * @return
     */
    @Override
    public ReturnData changeKitchen(@Valid @RequestBody Kitchen kitchen, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //菜系最多选四个
        String[] cs = kitchen.getCuisine().split(",");
        if (cs.length >= 5) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "菜系最多选四个", new JSONObject());
        }
        kitchenService.updateKitchen(kitchen);
        if (!CommonUtils.checkFull(kitchen.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(kitchen.getUserId(), kitchen.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除厨房
     * @return:
     */
    @Override
    public ReturnData delKitchen(@PathVariable long userId, @PathVariable long id) {
        Kitchen io = kitchenService.findByUserId(userId);
        if (io != null) {
            io.setDeleteType(1);
            kitchenService.updateDel(io);
            //同时删除该厨房下的菜品
            kitchenService.deleteFood(userId, id);
        }
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新厨房营业状态
     * @param kitchen
     * @return
     */
    @Override
    public ReturnData updKitchenStatus(@Valid @RequestBody Kitchen kitchen, BindingResult bindingResult) {
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + kitchen.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(kitchen.getUserId());
            if (userAccountSecurity == null) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + kitchen.getUserId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        kitchenService.updateBusiness(kitchen);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询厨房信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findKitchen(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Kitchen kitchen = kitchenService.findByUserId(userId);
            if (kitchen == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            UserInfo sendInfoCache = null;
            sendInfoCache = userInfoUtils.getUserInfo(userId);
            if (sendInfoCache != null) {
                if (userId == CommonUtils.getMyId()) {//查看自己店铺时返回的是实名信息
                    //检测是否实名
                    Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
                    if (map == null || map.size() <= 0) {
                        UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
                        if (userAccountSecurity != null) {
                            userAccountSecurity.setRedisStatus(1);//数据库中已有记录
                            //放到缓存中
                            map = CommonUtils.objectToMap(userAccountSecurity);
                            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
                        }
                    }
                    if (map != null || map.size() > 0) {
                        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
                        if (userAccountSecurity != null) {
                            if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) || !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                                kitchen.setName(userAccountSecurity.getRealName());
                                kitchen.setSex(CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard()));
                                kitchen.setAge(CommonUtils.getAgeByIdCard(userAccountSecurity.getIdCard()));
                            }
                        }
                    }
                } else {
                    kitchen.setName(sendInfoCache.getName());
                }
                kitchen.setHead(sendInfoCache.getHead());
                kitchen.setProTypeId(sendInfoCache.getProType());
                kitchen.setHouseNumber(sendInfoCache.getHouseNumber());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 条件查询厨房
     * @param lat      纬度
     * @param lon      经度
     * @param kitchenName    厨房名称
     * @param page     页码
     * @param count    条数
     * @param sortType 排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高  4视频
     * @return
     */
    @Override
    public ReturnData findKitchenList(@PathVariable int sortType, @PathVariable String kitchenName, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        int raidus = 10000;    //半径/ M
        PageBean<Kitchen> pageBean = null;
        pageBean = kitchenService.findKitchenList(CommonUtils.getMyId(), sortType, lat, lon, raidus, kitchenName, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Kitchen ik = (Kitchen) list.get(i);

                double userlon = Double.valueOf(ik.getLon() + "");
                double userlat = Double.valueOf(ik.getLat() + "");

                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

                ik.setDistance(distance);//距离/m
                //过滤实名信息
                ik.setName("");
                ik.setSex(0);
                ik.setAge(0);

                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                if (sendInfoCache != null) {
                    ik.setName(sendInfoCache.getName());
                    ik.setHead(sendInfoCache.getHead());
                    ik.setProTypeId(sendInfoCache.getProType());
                    ik.setHouseNumber(sendInfoCache.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 检测实名状态
     * @param userId
     * @return
     */
    @Override
    public ReturnData realNameStatus(@PathVariable long userId) {
        int age = 0;
        int sex = 0;
        String name = "";
        int autonym = 0;//0未实名，1已实名
        //检测是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
            if (userAccountSecurity != null) {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
                //放到缓存中
                map = CommonUtils.objectToMap(userAccountSecurity);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
            }
        }
        Map<String, Object> map2 = new HashMap<>();
        if (map != null || map.size() > 0) {
            UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
            if (userAccountSecurity != null) {
                if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) || !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                    autonym = 1;//已实名
                    name = userAccountSecurity.getRealName();//姓名
                    sex = CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard());//性别
                    age = CommonUtils.getAgeByIdCard(userAccountSecurity.getIdCard());//年龄
                    map2.put("age", age);//年龄
                    map2.put("sex", sex);//性别
                    map2.put("name", name);//姓名
                }
            }
        }
        map2.put("autonym", autonym);//实名状态
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 新增厨房收藏
     * @param collect
     * @return
     */
    @Override
    public ReturnData addKitchenCollect(@Valid @RequestBody KitchenCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否收藏过
        boolean flag = kitchenService.findWhether(collect.getUserId(), collect.getKitchend());
        if (flag) {
            return returnData(StatusCode.CODE_COLLECTED_KITCHEN_ERROR.CODE_VALUE, "您已收藏过此厨房", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + collect.getBeUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Kitchen kitchen2 = kitchenService.findByUserId(collect.getBeUserId());
            if (kitchen2 == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，厨房不存在！", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen2);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
        }
        Kitchen io = (Kitchen) CommonUtils.mapToObject(kitchenMap, Kitchen.class);
        if (io != null) {
            //添加收藏记录
            collect.setCuisine(io.getCuisine());
            collect.setDistance(io.getDistance());
            collect.setGoodFood(io.getGoodFood());
            collect.setKitchend(io.getId());
            collect.setKitchenName(io.getKitchenName());
            collect.setKitchenCover(io.getKitchenCover());
            collect.setTime(new Date());

            kitchenService.addCollect(collect);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询用户收藏列表
     * @param userId   用户ID
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findKitchenCollectList(@PathVariable long userId, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenCollection> pageBean;
        pageBean = kitchenService.findCollectionList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        List ktchenList = null;
        list = pageBean.getList();
        String kitchendIds = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                KitchenCollection kc = (KitchenCollection) list.get(i);
                if (i == 0) {
                    kitchendIds = kc.getKitchend() + "";//厨房ID
                } else {
                    kitchendIds += "," + kc.getKitchend();
                }
            }
            //查詢厨房
            ktchenList = kitchenService.findKitchenList4(kitchendIds.split(","));
            if (ktchenList != null) {
                for (int i = 0; i < ktchenList.size(); i++) {
                    Kitchen ik = (Kitchen) ktchenList.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        KitchenCollection kc = (KitchenCollection) list.get(j);
                        if (ik != null && kc != null) {
                            if (ik.getId() == kc.getKitchend()) {
                                double userlon = Double.valueOf(ik.getLon() + "");
                                double userlat = Double.valueOf(ik.getLat() + "");
                                //计算距离
                                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));
                                kc.setDistance(distance);//距离/m
                                kc.setAddress(ik.getAddress());//详细地址
                            }
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /**
     * @Description: 删除收藏
     * @return:
     */
    @Override
    public ReturnData delKitchenCollect(@PathVariable String ids) {
        //查询数据库
        kitchenService.del(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增菜品
     * @param kitchenDishes
     * @return
     */
    @Override
    public ReturnData addFood(@Valid @RequestBody KitchenDishes kitchenDishes, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenDishes.setAddTime(new Date());
        kitchenService.addDishes(kitchenDishes);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", kitchenDishes.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @Override
    public ReturnData updateFood(@Valid @RequestBody KitchenDishes kitchenDishes, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenService.updateDishes(kitchenDishes);
        if (!CommonUtils.checkFull(kitchenDishes.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(kitchenDishes.getUserId(), kitchenDishes.getDelImgUrls());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", kitchenDishes.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 删除菜品
     * @return:
     */
    @Override
    public ReturnData delFood(@PathVariable String ids) {
        //查询数据库
        kitchenService.delDishes(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询菜品信息
     * @param id
     * @return
     */
    @Override
    public ReturnData disheSdetails(@PathVariable long id) {
        KitchenDishes dishes = kitchenService.disheSdetails(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", dishes);
    }

    /***
     * 分页查询菜品列表
     * @param kitchenId   厨房ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findDishesList(@PathVariable long kitchenId, @PathVariable int page, @PathVariable int count) {
        int collection = 0;//是否收藏过此厨房  0没有  1已收藏
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenDishes> pageBean = null;
        pageBean = kitchenService.findDishesList(kitchenId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null) {
            //验证是否收藏过
            boolean flag = kitchenService.findWhether(CommonUtils.getMyId(), kitchenId);
            if (flag) {
                collection = 1;//1已收藏
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        map.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    //根据生日计算年龄
    public int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                throw new IllegalArgumentException("年龄不能超过当前日期");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
            int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
            if (nowDayOfYear < bornDayOfYear) {
                age -= 1;
            }
        }
        return age;
    }

}
