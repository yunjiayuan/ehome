package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HotelService;
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
 * @description: 酒店民宿相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 15:06:11
 */
@RestController
public class HotelController extends BaseController implements HotelApiController {
    @Autowired
    HotelService travelService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    /***
     * 新增酒店民宿
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHotel(@Valid @RequestBody Hotel scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有酒店民宿
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOTEL + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Hotel kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_HOTEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        Hotel ik = (Hotel) CommonUtils.mapToObject(kitchenMap, Hotel.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "入驻酒店民宿失败，酒店民宿已存在！", new JSONObject());
        }
        scenicSpot.setAuditType(0);
        scenicSpot.setBusinessStatus(1);//酒店民宿默认关闭
        scenicSpot.setAddTime(new Date());
        travelService.addKitchen(scenicSpot);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", scenicSpot.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新酒店民宿
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeHotel(@Valid @RequestBody Hotel scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(scenicSpot.getHotelName()) && !CommonUtils.checkFull(scenicSpot.getLicence())) {//上传酒店民宿证照
            scenicSpot.setAuditType(1);
            travelService.updateKitchen2(scenicSpot);
        } else {
            travelService.updateKitchen(scenicSpot);
        }
        if (!CommonUtils.checkFull(scenicSpot.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(scenicSpot.getUserId(), scenicSpot.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOTEL + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param userId
     * @param id
     * @Description: 删除酒店民宿
     * @return:
     */
    @Override
    public ReturnData delHotel(@PathVariable long userId, @PathVariable long id) {
        Hotel io = travelService.findById(id, 0);
        if (io != null) {
            io.setDeleteType(1);
            travelService.updateDel(io);
            //同时删除该酒店民宿的酒店民宿房间
            travelService.delHotel(userId, id);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_HOTEL + userId, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新酒店民宿营业状态
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updHotelStatus(@Valid @RequestBody Hotel scenicSpot, BindingResult bindingResult) {
        //判断该酒店民宿是否有证照
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOTEL + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Hotel kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_HOTEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        Hotel ik = (Hotel) CommonUtils.mapToObject(kitchenMap, Hotel.class);
        if (ik == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "酒店民宿不存在！", new JSONObject());
        }
        if (ik.getAuditType() != 1) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "该酒店民宿未上传酒店民宿证照", new JSONObject());
        }
        travelService.updateBusiness(scenicSpot);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_HOTEL + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询酒店民宿信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findHotel(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOTEL + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Hotel kitchen = travelService.findReserve(userId);
            if (kitchen == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
//            UserInfo sendInfoCache = null;
//            sendInfoCache = userInfoUtils.getUserInfo(userId);
//            if (sendInfoCache != null) {
//                kitchen.setName(sendInfoCache.getName());
//                kitchen.setHead(sendInfoCache.getHead());
//                kitchen.setProTypeId(sendInfoCache.getProType());
//                kitchen.setHouseNumber(sendInfoCache.getHouseNumber());
//            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen);
            redisUtils.hmset(Constants.REDIS_KEY_HOTEL + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        int collection = 0;//是否收藏过此酒店民宿  0没有  1已收藏
        if (kitchenMap != null && kitchenMap.size() > 0) {
            //验证是否收藏过
            boolean flag = travelService.findWhether(CommonUtils.getMyId(), userId);
            if (flag) {
                collection = 1;//1已收藏
            }
        }
        kitchenMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 条件查询酒店民宿
     * @param watchVideos 筛选视频：0否 1是
     * @param hotelType 筛选：-1全部 0酒店 1民宿
     * @param name    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHotelList(@PathVariable int watchVideos, @PathVariable int hotelType, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<Hotel> pageBean = null;
        pageBean = travelService.findKitchenList(CommonUtils.getMyId(), hotelType, watchVideos, name, province, city, district, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Hotel ik = (Hotel) list.get(i);
                int distance = (int) Math.round(CommonUtils.getShortestDistance(ik.getLon(), ik.getLat(), lon, lat));
                ik.setDistance(distance);//距离/m
//                UserInfo sendInfoCache = null;
//                sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
//                if (sendInfoCache != null) {
//                    ik.setName(sendInfoCache.getName());
//                    ik.setHead(sendInfoCache.getHead());
//                    ik.setProTypeId(sendInfoCache.getProType());
//                    ik.setHouseNumber(sendInfoCache.getHouseNumber());
//                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 新增酒店民宿房间
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHotelRoom(@Valid @RequestBody HotelRoom tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        tickets.setAddTime(new Date());
        Hotel kitchen = travelService.findById(tickets.getHotelId(), tickets.getType());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "酒店或景区不存在", new JSONObject());
        }
        travelService.addDishes(tickets);
        if (tickets.getType() == 0) { // 所属类型：0酒店 1景区
            List list = null;
            list = travelService.findList(tickets.getHotelId(), tickets.getType());
            if (list != null && list.size() > 0) {
                HotelRoom tickets1 = (HotelRoom) list.get(0);
                if (tickets1 != null) {
                    kitchen.setCost(tickets1.getCost());
                }
            } else {
                kitchen.setCost(tickets.getCost());
            }
            travelService.updateKitchen3(kitchen);
            //清除酒店缓存
            redisUtils.expire(Constants.REDIS_KEY_HOTEL + kitchen.getUserId(), 0);
        }
        //清除缓存中的酒店民宿房间信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELROOMLIST + tickets.getHotelId() + "_" + tickets.getType(), 0);

        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新酒店民宿房间
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updateHotelRoom(@Valid @RequestBody HotelRoom tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        Hotel kitchen = travelService.findById(tickets.getHotelId(), tickets.getType());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "酒店或景区不存在", new JSONObject());
        }
        travelService.updateDishes(tickets);
        if (tickets.getType() == 0) { // 所属类型：0酒店 1景区
            List list = null;
            list = travelService.findList(tickets.getHotelId(), tickets.getType());
            if (list != null && list.size() > 0) {
                HotelRoom tickets1 = (HotelRoom) list.get(0);
                if (tickets1 != null) {
                    kitchen.setCost(tickets1.getCost());
                }
            } else {
                kitchen.setCost(tickets.getCost());
            }
            travelService.updateKitchen3(kitchen);
            //清除酒店缓存
            redisUtils.expire(Constants.REDIS_KEY_HOTEL + kitchen.getUserId(), 0);
        }
        //清除缓存中的酒店民宿房间信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELROOMLIST + tickets.getHotelId() + "_" + tickets.getType(), 0);

        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @param ids
     * @Description: 删除酒店民宿房间
     * @return:
     */
    @Override
    public ReturnData delHotelRoom(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        HotelRoom dishes = travelService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //查询数据库
        travelService.delDishes(idss, CommonUtils.getMyId());
        List list = null;
        Hotel kitchen = new Hotel();
        list = travelService.findList(dishes.getHotelId(), dishes.getType());
        if (list != null && list.size() > 0) {
            HotelRoom tickets1 = (HotelRoom) list.get(0);
            kitchen.setCost(tickets1.getCost());
        } else {
            kitchen.setCost(0);
        }
        kitchen.setUserId(dishes.getUserId());
        kitchen.setId(dishes.getHotelId());
        travelService.updateKitchen3(kitchen);
        //清除酒店缓存
        redisUtils.expire(Constants.REDIS_KEY_HOTEL + dishes.getUserId(), 0);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELROOMLIST + dishes.getHotelId() + "_" + dishes.getType(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询房间详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findHotelRoom(@PathVariable long id) {
        HotelRoom reserveData = travelService.disheSdetails(id);
        Map<String, Object> map = new HashMap<>();
        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 分页查询房间列表
     * @param type  查询类型; 0酒店 1景区
     * @param id   酒店或景区ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHotelRoomList(@PathVariable int type, @PathVariable long id, @PathVariable int page, @PathVariable int count) {
        Hotel io = null;
        int collection = 0;//是否收藏过此景区  0没有  1已收藏
        List<HotelRoom> cartList = null;
        io = travelService.findById(id, type);
        if (io == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "酒店或景区不存在", new JSONObject());
        }
        //从缓存中获取房间列表
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_HOTELROOMLIST + id + "_" + type);
        if (map == null || map.size() <= 0) {
            //查询数据库
            cartList = travelService.findList(id, type);
            map.put("data", cartList);
            //更新到缓存
            redisUtils.hmset(Constants.REDIS_KEY_HOTELROOMLIST + id + "_" + type, map, Constants.USER_TIME_OUT);
        }
        if (type == 0) { // 所属类型：0酒店 1景区
            //验证是否收藏过
            boolean flag = travelService.findWhether(CommonUtils.getMyId(), io.getUserId());
            if (flag) {
                collection = 1;//1已收藏
            }
            map.put("collection", collection);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, map);
    }

    /***
     * 新增收藏
     * @param collect
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHotelCollect(@Valid @RequestBody HotelCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否收藏过
        boolean flag = travelService.findWhether(collect.getMyId(), collect.getUserId());
        if (flag) {
            return returnData(StatusCode.CODE_COLLECTED_HOURLY_ERROR.CODE_VALUE, "您已收藏过此酒店民宿", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOTEL + collect.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Hotel kitchen2 = travelService.findReserve(collect.getUserId());
            if (kitchen2 == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，酒店民宿不存在！", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen2);
            redisUtils.hmset(Constants.REDIS_KEY_HOTEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
        }
        Hotel io = (Hotel) CommonUtils.mapToObject(kitchenMap, Hotel.class);
        if (io != null) {
            //添加收藏记录
            collect.setTime(new Date());
            if (!CommonUtils.checkFull(io.getPicture())) {
                String[] strings = io.getPicture().split(",");
                collect.setPicture(strings[0]);
            }
            collect.setName(io.getHotelName());
            travelService.addCollect(collect);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询收藏列表
     * @param userId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHotelCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HotelCollection> pageBean;
        pageBean = travelService.findCollectionList(userId, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @param ids
     * @Description: 删除收藏
     * @return:
     */
    @Override
    public ReturnData delHotelCollect(@PathVariable String ids) {
        //查询数据库
        travelService.del(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增酒店数据
     * @param kitchenData
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHotelData(@Valid @RequestBody HotelData kitchenData, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(kitchenData.getName())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        HotelData reserveData = travelService.findReserveDataId(kitchenData.getUid());
        Hotel reserve = new Hotel();
        reserve.setAddTime(new Date());
        reserve.setAddress(kitchenData.getAddress());
        reserve.setClaimId(kitchenData.getUid());
        reserve.setHotelName(kitchenData.getName());
        reserve.setLat(kitchenData.getLatitude());
        reserve.setLon(kitchenData.getLongitude());
        reserve.setPhone(kitchenData.getPhone());
        reserve.setTotalScore(kitchenData.getOverallRating());
        reserve.setAuditType(1);
//        reserve.setBusinessStatus(1);//酒店默认关闭
        if (reserveData == null) {//新增
            //新增酒店数据表
            kitchenData.setAddTime(new Date());
            travelService.addReserveData(kitchenData);
            //新增酒店表
            travelService.addKitchen(reserve);
        } else {//更新
            //更新酒店数据表
            travelService.updateReserveData(kitchenData);
            //更新酒店表
            travelService.updateKitchen(reserve);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询酒店数据详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findHotelData(@PathVariable long id) {
        HotelData reserveData = travelService.findReserveData(id);
        Map<String, Object> map = new HashMap<>();
        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 入驻酒店民宿
     * @param kitchenReserve
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData claimHotel(@Valid @RequestBody Hotel kitchenReserve, BindingResult bindingResult) {
        HotelData kitchen = travelService.findReserveDataId(kitchenReserve.getClaimId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "入驻酒店民宿不存在", new JSONObject());
        }
        if (kitchen.getClaimStatus() == 1) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "入驻酒店民宿不存在", new JSONObject());
        }
        //更新酒店数据
        kitchen.setClaimStatus(1);
        kitchen.setClaimTime(new Date());
        kitchen.setUserId(CommonUtils.getMyId());
        travelService.claimKitchen(kitchen);
        //更新酒店
        Hotel reserve = new Hotel();
        reserve.setPhone(kitchen.getPhone());
        reserve.setLicence(kitchenReserve.getLicence());
        reserve.setClaimId(kitchen.getUid());
        reserve.setClaimStatus(1);
        reserve.setClaimTime(kitchen.getClaimTime());
        reserve.setUserId(CommonUtils.getMyId());
        travelService.claimKitchen2(reserve);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询酒店数据列表
     * @param hotelType  -1不限  0酒店 1民宿
     * @param name    酒店名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHotelDataList(@PathVariable int hotelType, @PathVariable String name, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HotelData> pageBean = null;
        pageBean = travelService.findReserveDataList(hotelType, name, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            if (CommonUtils.checkFull(name) && lat > 0) {//距离最近
                for (int i = 0; i < list.size(); i++) {
                    HotelData ik = (HotelData) list.get(i);
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(ik.getLongitude(), ik.getLatitude(), lon, lat));
                    ik.setDistance(distance);//距离/m
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

}
