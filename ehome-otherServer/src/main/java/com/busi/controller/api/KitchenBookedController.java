package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.KitchenBookedOrdersService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 厨房订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 17:51
 */
@RestController
public class KitchenBookedController extends BaseController implements KitchenBookedApiController {

    @Autowired
    KitchenBookedService kitchenBookedService;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    KitchenBookedOrdersService kitchenBookedOrdersService;

    /***
     * 新增可预订厨房
     * @param kitchenReserve
     * @return
     */
    @Override
    public ReturnData addReserve(@Valid @RequestBody KitchenReserve kitchenReserve, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有厨房
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + kitchenReserve.getUserId() + "_" + 1);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            KitchenReserve kitchen2 = kitchenBookedService.findReserve(kitchenReserve.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen2.getUserId() + "_" + 1, kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        KitchenReserve ik = (KitchenReserve) CommonUtils.mapToObject(kitchenMap, KitchenReserve.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增可预订厨房失败，厨房已存在！", new JSONObject());
        }
        kitchenReserve.setAuditType(1);
        kitchenReserve.setBusinessStatus(1);//厨房默认关闭
        kitchenReserve.setAddTime(new Date());
        //菜系最多选四个
        String[] cs = kitchenReserve.getCuisine().split(",");
        if (cs.length >= 5) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "菜系最多选四个", new JSONObject());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(kitchenReserve.getUserId());
        if (userInfo != null) {
            kitchenReserve.setSex(userInfo.getSex());
            kitchenReserve.setName(userInfo.getName());
            kitchenReserve.setAge(getAge(userInfo.getBirthday()));//年龄
        }
        kitchenBookedService.addKitchen(kitchenReserve);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", kitchenReserve.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 编辑可预订厨房
     * @param kitchenReserve
     * @return
     */
    @Override
    public ReturnData changeReserve(@Valid @RequestBody KitchenReserve kitchenReserve, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //菜系最多选四个
        String[] cs = kitchenReserve.getCuisine().split(",");
        if (cs.length >= 5) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "菜系最多选四个", new JSONObject());
        }
        kitchenBookedService.updateKitchen(kitchenReserve);
        if (!CommonUtils.checkFull(kitchenReserve.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(kitchenReserve.getUserId(), kitchenReserve.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kitchenReserve.getUserId() + "_" + 1, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除可预订厨房
     * @return:
     */
    @Override
    public ReturnData delReserve(@PathVariable long userId, @PathVariable long id) {
        KitchenReserve io = kitchenBookedService.findById(id);
        if (io != null) {
            io.setDeleteType(1);
            kitchenBookedService.updateDel(io);
            //同时删除该厨房下的菜品
            kitchenService.deleteFood(userId, id);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_KITCHEN + userId + "_" + 1, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新可预订厨房营业状态
     * @param kitchenReserve
     * @return
     */
    @Override
    public ReturnData updReserveStatus(@Valid @RequestBody KitchenReserve kitchenReserve, BindingResult bindingResult) {
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + kitchenReserve.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(kitchenReserve.getUserId());
            if (userAccountSecurity == null) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + kitchenReserve.getUserId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        //是否设置订座设置
        KitchenBooked kitchen = kitchenBookedService.findByUserId(kitchenReserve.getUserId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_BOOKED_KITCHEN_ERROR.CODE_VALUE, "未设置订座相关设置", new JSONObject());
        }
        kitchenBookedService.updateBusiness(kitchenReserve);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kitchenReserve.getUserId() + "_" + 1, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询可预订厨房信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findReserve(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + userId + "_" + 1);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            KitchenReserve kitchen = kitchenBookedService.findReserve(userId);
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
            redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + userId + "_" + 1, kitchenMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 条件查询厨房订座
     * @param cuisine    菜系
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType 排序类型：默认【0综合排序】   0综合排序  1距离最近  2服务次数最高  3评分最高
     * @param kitchenName    厨房名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findReserveList(@PathVariable String cuisine, @PathVariable int watchVideos, @PathVariable int sortType, @PathVariable String kitchenName, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenReserve> pageBean = null;
        pageBean = kitchenBookedService.findKitchenList(CommonUtils.getMyId(), cuisine, watchVideos, sortType, kitchenName, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                KitchenReserve ik = (KitchenReserve) list.get(i);

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
                Collections.sort(list, new Comparator<KitchenReserve>() {
                    /*
                     * int compare(Person o1, Person o2) 返回一个基本类型的整型，
                     * 返回负数表示：o1 小于o2，
                     * 返回0 表示：o1和p2相等，
                     * 返回正数表示：o1大于o2
                     */
                    @Override
                    public int compare(KitchenReserve o1, KitchenReserve o2) {
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
     * 新增订座设置信息
     * @param kitchenBooked
     * @return
     */
    @Override
    public ReturnData addKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        KitchenBooked kitchen = kitchenBookedService.findByUserId(kitchenBooked.getUserId());
        if (kitchen != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "你已经设置过了", new JSONObject());
        }
        //新增厨房订座信息
        kitchenBookedService.add(kitchenBooked);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订座设置详情
     * @param userId  商家ID
     * @return
     */
    @Override
    public ReturnData findKitchenBooked(@PathVariable long userId) {
        KitchenBooked kitchen = kitchenBookedService.findByUserId(userId);
//        if (kitchen == null) {
//            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchen);
    }

    /***
     * 编辑订座设置
     * @param kitchenBooked
     * @return
     */
    @Override
    public ReturnData changeKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenBookedService.updateBooked(kitchenBooked);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增包间or大厅信息
     * @param kitchenPrivateRoom
     * @return
     */
    @Override
    public ReturnData addPrivateRoom(@Valid @RequestBody KitchenPrivateRoom kitchenPrivateRoom, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(kitchenPrivateRoom.getImgUrl())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (kitchenPrivateRoom.getImgUrl().split(",").length > 9) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //新增厨房包间or大厅信息
        kitchenBookedService.addPrivateRoom(kitchenPrivateRoom);
        //更新厨房包间or大厅数量（+1）
        KitchenBooked booked = kitchenBookedService.findByUserId(kitchenPrivateRoom.getUserId());
        if (booked != null) {
            if (kitchenPrivateRoom.getBookedType() == 1) {//就餐位置 包间0  散桌1
                booked.setLooseTableTotal(booked.getLooseTableTotal() + 1);
            } else {
                booked.setRoomsTotal(booked.getRoomsTotal() + 1);
            }
            kitchenBookedService.updatePosition(booked);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询包间or大厅信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findRoom(@PathVariable long id) {
        KitchenPrivateRoom privateRoom = kitchenBookedService.findPrivateRoom(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", privateRoom);
    }

    /***
     * 查看包间or大厅列表
     * @param eatTime  就餐时间
     * @param userId  商家ID
     * @param bookedType  包间0  散桌1
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPrivateRoom(@PathVariable String eatTime, @PathVariable long userId, @PathVariable int bookedType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenPrivateRoom> pageBean = null;
        pageBean = kitchenBookedService.findRoomList(userId, bookedType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null && list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        if (CommonUtils.checkFull(eatTime)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(eatTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //查询订单中已订餐桌（用来判断是否可预订，PS:有可能不同时段不同人预定）
        List ordersList = null;
        ordersList = kitchenBookedOrdersService.findOrdersList(userId, date, bookedType);
        if (ordersList == null && ordersList.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
        }
        for (int i = 0; i < list.size(); i++) {
            KitchenPrivateRoom privateRoom = (KitchenPrivateRoom) list.get(i);
            if (privateRoom == null) {
                continue;
            }
            for (int j = 0; j < ordersList.size(); j++) {
                KitchenBookedOrders kh = (KitchenBookedOrders) ordersList.get(j);
                if (kh == null) {
                    continue;
                }
                if (kh.getPositionId() == privateRoom.getId()) {
                    privateRoom.setReserveState(1);//已预定
                    continue;
                }
            }
        }
        //按是否预定正序排序
        Collections.sort(list, new Comparator<KitchenPrivateRoom>() {
            /*
             * int compare(Person o1, Person o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和p2相等，
             * 返回正数表示：o1大于o2
             */
            @Override
            public int compare(KitchenPrivateRoom o1, KitchenPrivateRoom o2) {
                // 按照预定与否进行正序排列
                if (o1.getReserveState() > o2.getReserveState()) {
                    return 1;
                }
                if (o1.getReserveState() == o2.getReserveState()) {
                    return 0;
                }
                return -1;
            }
        });
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 编辑包间or大厅信息
     * @param kitchenPrivateRoom
     * @return
     */
    @Override
    public ReturnData changePrivateRoom(@Valid @RequestBody KitchenPrivateRoom kitchenPrivateRoom, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenBookedService.upPrivateRoom(kitchenPrivateRoom);
        if (!CommonUtils.checkFull(kitchenPrivateRoom.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(kitchenPrivateRoom.getUserId(), kitchenPrivateRoom.getDelImgUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除包间or大厅
     * @return:
     */
    @Override
    public ReturnData delPrivateRoom(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        KitchenPrivateRoom privateRoom = kitchenBookedService.findPrivateRoom(id);
        if (privateRoom == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //更新厨房包间or大厅数量（+1）
        KitchenBooked booked = kitchenBookedService.findByUserId(privateRoom.getUserId());
        if (booked != null) {
            if (privateRoom.getBookedType() == 1) {// 包间0  散桌1
                booked.setLooseTableTotal(booked.getLooseTableTotal() - idss.length);
            } else {
                booked.setRoomsTotal(booked.getRoomsTotal() - idss.length);
            }
            kitchenBookedService.updatePosition(booked);
        }
        //删除数据库
        kitchenBookedService.delPrivateRoom(idss, CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增菜品
     * @param kitchenDishes
     * @return
     */
    @Override
    public ReturnData addDishes(@Valid @RequestBody KitchenReserveDishes kitchenDishes, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenDishes.setAddTime(new Date());
        kitchenBookedService.addDishes(kitchenDishes);

        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenDishes.getKitchenId() + "_" + 1, 0);
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
    public ReturnData updateDishes(@Valid @RequestBody KitchenReserveDishes kitchenDishes, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenBookedService.updateDishes(kitchenDishes);
        if (!CommonUtils.checkFull(kitchenDishes.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(kitchenDishes.getUserId(), kitchenDishes.getDelImgUrls());
        }
        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenDishes.getKitchenId() + "_" + 1, 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", kitchenDishes.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 删除菜品
     * @return:
     */
    @Override
    public ReturnData delDishes(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        KitchenReserveDishes dishes = kitchenBookedService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + dishes.getKitchenId() + "_" + 1, 0);

        //查询数据库
        kitchenBookedService.delDishes(idss, CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询菜品信息
     * @param id
     * @return
     */
    @Override
    public ReturnData detailsDishes(@PathVariable long id) {
        KitchenReserveDishes dishes = kitchenBookedService.disheSdetails(id);

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
    public ReturnData findReserveDishesList(@PathVariable long kitchenId, @PathVariable int page, @PathVariable int count) {
        Map<String, Object> collectionMap = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();//最终组合后List
        //从缓存中获取菜品列表
        collectionMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHENDISHESLIST + 1);
        if (collectionMap != null && collectionMap.size() > 0) {//缓存中存在 直接返回
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, collectionMap);
        }
        //清除缓存中的菜品信息  防止并发
        redisUtils.expire(Constants.REDIS_KEY_KITCHENDISHESLIST + 1, 0);
        //查询是否收藏过此厨房
        int collection = 0;//是否收藏过此厨房  0没有  1已收藏
        boolean flag = kitchenService.findWhether2(1, CommonUtils.getMyId(), kitchenId);
        if (flag) {
            collection = 1;//1已收藏
        }
        collectionMap.put("collection", collection);
        //查询菜品
        List list = null;
        list = kitchenBookedService.findDishesList2(kitchenId);//全部返回
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, collectionMap);
        }
        //查询菜品分类
        List sortList = null;
        sortList = kitchenService.findDishesSortList2(1, kitchenId);
        if (sortList == null || sortList.size() <= 0) {
            collectionMap.put("list", list);
            //更新到缓存
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenId + "_" + 1, collectionMap);
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
                KitchenReserveDishes dishes = (KitchenReserveDishes) list.get(j);
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
        redisUtils.hmset(Constants.REDIS_KEY_KITCHENDISHESLIST + kitchenId + "_" + 1, collectionMap);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", collectionMap);
    }

    /***
     * 新增上菜时间
     * @param kitchenServingTime
     * @return
     */
    @Override
    public ReturnData addUpperTime(@Valid @RequestBody KitchenServingTime kitchenServingTime, BindingResult bindingResult) {
        //开始查询
        KitchenServingTime servingTime = kitchenBookedService.findUpperTime(kitchenServingTime.getKitchenId());
        if (servingTime != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您已新增过", new JSONArray());
        }
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String[] time = kitchenServingTime.getUpperTime().split(",");
        if (time.length > 15) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "最多只能添加15个预约时间", new JSONObject());
        }
        KitchenBooked kitchen = kitchenBookedService.findByUserId(kitchenServingTime.getUserId());
        if (kitchen != null) {
            for (int i = 0; i < time.length; i++) {
                if (time[i].compareTo(kitchen.getEarliestTime()) < 0 || kitchen.getLatestTime().compareTo(time[i]) < 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
                }
            }
        }
        kitchenBookedService.addUpperTime(kitchenServingTime);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新上菜时间
     * @param kitchenServingTime
     * @return
     */
    @Override
    public ReturnData updateUpperTime(@Valid @RequestBody KitchenServingTime kitchenServingTime, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String[] time = kitchenServingTime.getUpperTime().split(",");
        if (time.length > 15) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "最多只能添加15个预约时间", new JSONObject());
        }
        KitchenBooked kitchen = kitchenBookedService.findByUserId(kitchenServingTime.getUserId());
        if (kitchen != null) {
            for (int i = 0; i < time.length; i++) {//上菜时间要在营业时间范围内
                if (time[i].compareTo(kitchen.getEarliestTime()) < 0 || kitchen.getLatestTime().compareTo(time[i]) < 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
                }
            }
        }
        kitchenBookedService.updateUpperTime(kitchenServingTime);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询上菜时间
     * @param kitchenId   厨房ID
     * @return
     */
    @Override
    public ReturnData findUpperTime(@PathVariable long kitchenId) {
        //开始查询
        KitchenServingTime servingTime = kitchenBookedService.findUpperTime(kitchenId);
//        if (servingTime == null) {
//            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", servingTime);
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
