package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.KitchenBookedService;
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
    KitchenBookedService kitchenBookedService;

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
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 0);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Kitchen kitchen2 = kitchenService.findByUserId(kitchen.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen2.getUserId() + "_" + 0, kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        Kitchen ik = (Kitchen) CommonUtils.mapToObject(kitchenMap, Kitchen.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增厨房失败，厨房已存在！", new JSONObject());
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
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 0, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除厨房
     * @return:
     */
    @Override
    public ReturnData delKitchen(@PathVariable long userId, @PathVariable long id) {
        Kitchen io = kitchenService.findById(id);
        if (io != null) {
            io.setDeleteType(1);
            kitchenService.updateDel(io);
            //同时删除该厨房下的菜品
            kitchenService.deleteFood(userId, id);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_KITCHEN + userId + "_" + 0, 0);
        }
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
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 0, 0);
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
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + userId + "_" + 0);
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
            redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + userId + "_" + 0, kitchenMap, Constants.USER_TIME_OUT);
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
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType  排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高
     * @return
     */
    @Override
    public ReturnData findKitchenList(@PathVariable int watchVideos, @PathVariable int sortType, @PathVariable String kitchenName, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
//        int raidus = 10000;    //半径/ M
        PageBean<Kitchen> pageBean = null;
        pageBean = kitchenService.findKitchenList(CommonUtils.getMyId(), watchVideos, sortType, kitchenName, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
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
            if (sortType == 1) {//距离最近
                Collections.sort(list, new Comparator<Kitchen>() {
                    /*
                     * int compare(Person o1, Person o2) 返回一个基本类型的整型，
                     * 返回负数表示：o1 小于o2，
                     * 返回0 表示：o1和p2相等，
                     * 返回正数表示：o1大于o2
                     */
                    @Override
                    public int compare(Kitchen o1, Kitchen o2) {
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
        boolean flag = kitchenService.findWhether(collect.getUserId(), collect.getBeUserId(), collect.getBookedState());
        if (flag) {
            return returnData(StatusCode.CODE_COLLECTED_KITCHEN_ERROR.CODE_VALUE, "您已收藏过此厨房", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + collect.getBeUserId() + "_" + collect.getBookedState());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            if (collect.getBookedState() == 0) {
                Kitchen kitchen = kitchenService.findByUserId(collect.getBeUserId());
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，厨房不存在！", new JSONObject());
                }
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen);
                redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 0, kitchenMap, Constants.USER_TIME_OUT);
            } else {
                KitchenReserve kitchen = kitchenBookedService.findReserve(collect.getBeUserId());
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，预定厨房不存在！", new JSONObject());
                }
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen);
                redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 1, kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        if (collect.getBookedState() == 0) {
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
        } else {
            KitchenReserve io = (KitchenReserve) CommonUtils.mapToObject(kitchenMap, KitchenReserve.class);
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
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询用户收藏列表
     * @param userId   用户ID
     * @param bookedState   0厨房  1订座
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findKitchenCollectList(@PathVariable long userId, @PathVariable int bookedState, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenCollection> pageBean;
        pageBean = kitchenService.findCollectionList(userId, bookedState, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        List ktchenList = null;
        list = pageBean.getList();
        String kitchendIds = "";
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        for (int i = 0; i < list.size(); i++) {
            KitchenCollection kc = (KitchenCollection) list.get(i);
            if (i == 0) {
                kitchendIds = kc.getKitchend() + "";//厨房ID
            } else {
                kitchendIds += "," + kc.getKitchend();
            }
        }
        //查詢厨房
        if (bookedState == 0) {
            ktchenList = kitchenService.findKitchenList4(kitchendIds.split(","));
            if (ktchenList == null || ktchenList.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
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
        } else {
            ktchenList = kitchenService.findKitchenList5(kitchendIds.split(","));
            if (ktchenList == null || ktchenList.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            for (int i = 0; i < ktchenList.size(); i++) {
                KitchenReserve ik = (KitchenReserve) ktchenList.get(i);
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

        //清除缓存中的菜品信息  防止并发
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenDishes.getKitchenId() + "_" + kitchenDishes.getBookedState(), 0);
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
        //清除缓存中的菜品信息  防止并发
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenDishes.getKitchenId() + "_" + kitchenDishes.getBookedState(), 0);
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
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        KitchenDishes dishes = kitchenService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + dishes.getKitchenId() + "_" + dishes.getBookedState(), 0);

        //查询数据库
        kitchenService.delDishes(idss, CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询菜品信息
     * @param id
     * @return
     */
    @Override
    public ReturnData disheSdetails(@PathVariable long id) {
        Map<String, Object> map = new HashMap<>();
        KitchenDishes dishes = kitchenService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        KitchenDishesSort dishesSort = kitchenService.findDishesSort(dishes.getSortId());
        if (dishesSort != null) {
            map.put("sortName", dishesSort.getName());
        }
        map.put("data", dishes);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 分页查询菜品列表
     * @param kitchenId   厨房ID
     * @param bookedState    0厨房  1订座
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findDishesList(@PathVariable int bookedState, @PathVariable long kitchenId, @PathVariable int page, @PathVariable int count) {
        //验证参数
//        if (page < 0 || count <= 0) {
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
//        }
        Map<String, Object> collectionMap = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();//最终组合后List
        //查询是否收藏过此厨房
        int collection = 0;//是否收藏过此厨房  0没有  1已收藏
        boolean flag = kitchenService.findWhether2(bookedState, CommonUtils.getMyId(), kitchenId);
        if (flag) {
            collection = 1;//1已收藏
        }
        //从缓存中获取菜品列表
        collectionMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenId + "_" + bookedState);
        if (collectionMap != null && collectionMap.size() > 0) {//缓存中存在 直接返回
            collectionMap.put("collection", collection);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, collectionMap);
        }
        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenId + "_" + bookedState, 0);
        //查询菜品
        List list = null;
//        PageBean<KitchenDishes> pageBean = null;
        list = kitchenService.findDishesList2(bookedState, kitchenId);//全部返回
//        list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, collectionMap);
        }
        //查询菜品分类
        List sortList = null;
        sortList = kitchenService.findDishesSortList2(bookedState, kitchenId);
        if (sortList == null || sortList.size() <= 0) {
            collectionMap.put("list", list);
            //更新到缓存
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenId + "_" + bookedState, collectionMap);
            collectionMap.put("collection", collection);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", collectionMap);
        }
        //组合数据
        for (int i = 0; i < sortList.size(); i++) {
            List dishesList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            KitchenDishesSort dishesSort = (KitchenDishesSort) sortList.get(i);
            if (dishesSort == null) {
                continue;
            }
            for (int j = list.size() - 1; j >= 0; j--) {
                KitchenDishes dishes = (KitchenDishes) list.get(j);
                if (dishes == null) {
                    continue;
                }
                if (dishes.getSortId() == dishesSort.getId()) {
                    dishesList.add(dishes);
                }
            }
            map.put("sortId", dishesSort.getId());//分类ID
            map.put("sortName", dishesSort.getName());//分类名
            map.put("dishesList", dishesList);//菜品集合
            newList.add(map);
        }
        collectionMap.put("newList", newList);
        //更新到缓存
        redisUtils.hmset(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenId + "_" + bookedState, collectionMap);
        collectionMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", collectionMap);
    }

    /***
     * 新增分类
     * @param kitchenDishesSort
     * @return
     */
    @Override
    public ReturnData addFoodSort(@Valid @RequestBody KitchenDishesSort kitchenDishesSort, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户分类数量 最多15个
        int num = kitchenService.findNum(kitchenDishesSort.getBookedState(), kitchenDishesSort.getKitchenId());
        if (num >= 15) {
            return returnData(StatusCode.CODE_DISHESSORT_KITCHEN_ERROR.CODE_VALUE, "菜品分类超过上限,拒绝新增！", new JSONObject());
        }
        kitchenService.addSort(kitchenDishesSort);

        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenDishesSort.getKitchenId() + "_" + kitchenDishesSort.getBookedState(), 0);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", kitchenDishesSort.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新分类
     * @param kitchenDishesSort
     * @return
     */
    @Override
    public ReturnData updateFoodSort(@Valid @RequestBody KitchenDishesSort kitchenDishesSort, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenService.updateDishesSort(kitchenDishesSort);

        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenDishesSort.getKitchenId() + "_" + kitchenDishesSort.getBookedState(), 0);

//        Map<String, Object> map = new HashMap<>();
//        map.put("infoId", kitchenDishesSort.getId());
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除分类
     * @return:
     */
    @Override
    public ReturnData delFoodSort(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        KitchenDishesSort dishesSort = kitchenService.findDishesSort(id);
        if (dishesSort == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + dishesSort.getKitchenId() + "_" + dishesSort.getBookedState(), 0);
        //数据库删除
        kitchenService.delFoodSort(idss, CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询分类列表
     * @param kitchenId   厨房ID
     * @param bookedState    0厨房  1订座
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findDishesSortList(@PathVariable int bookedState, @PathVariable long kitchenId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenDishesSort> pageBean = null;
        pageBean = kitchenService.findDishesSortList(bookedState, kitchenId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
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
