package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HotelService;
import com.busi.service.KitchenBookedService;
import com.busi.service.TravelService;
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
 * @description: 家门口旅游
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 13:20:45
 */
@RestController
public class TravelController extends BaseController implements TravelApiController {

    @Autowired
    TravelService travelService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    KitchenBookedService kitchenBookedService;

    @Autowired
    HotelService hotelService;

    /***
     * 新增景区
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addScenicSpot(@Valid @RequestBody ScenicSpot scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有景区
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ScenicSpot kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ScenicSpot ik = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经有自己的店铺了，可以切换其他账号再进行创建或入驻", new JSONObject());
        }
        scenicSpot.setAuditType(0);
        scenicSpot.setBusinessStatus(1);//景区默认关闭
        scenicSpot.setClaimStatus(1);//默认自己新增为已入驻
        scenicSpot.setAddTime(new Date());
        travelService.addKitchen(scenicSpot);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", scenicSpot.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新景区
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeScenicSpot(@Valid @RequestBody ScenicSpot scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(scenicSpot.getScenicSpotName()) && !CommonUtils.checkFull(scenicSpot.getLicence())) {
            scenicSpot.setAuditType(0);
            scenicSpot.setBusinessStatus(1);//打烊中
            travelService.updateKitchen2(scenicSpot);
        } else {
            travelService.updateKitchen(scenicSpot);
        }
        if (!CommonUtils.checkFull(scenicSpot.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(scenicSpot.getUserId(), scenicSpot.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVEL + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param userId
     * @param id
     * @Description: 删除景区
     * @return:
     */
    @Override
    public ReturnData delScenicSpot(@PathVariable long userId, @PathVariable long id) {
        ScenicSpot io = travelService.findById(id);
        if (io != null) {
            io.setDeleteType(1);
            travelService.updateDel(io);
            //同时删除该景区的门票
            travelService.delScenicSpot(userId, id);
            //清除缓存中的门票信息
            redisUtils.expire(Constants.REDIS_KEY_TRAVELTICKETSLIST + id, 0);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL + userId, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新景区营业状态
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updScenicSpotStatus(@Valid @RequestBody ScenicSpot scenicSpot, BindingResult bindingResult) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ScenicSpot kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ScenicSpot ik = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
        if (ik == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "景区不存在！", new JSONObject());
        }
        if (ik.getAuditType() == 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺正在审核中，审核通过后才能正常营业，请耐心等待", new JSONObject());
        }
        if (ik.getAuditType() == 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺审核失败，请重新上传清晰、准确、合法的证照", new JSONObject());
        }
        travelService.updateBusiness(scenicSpot);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_TRAVEL + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询景区信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findScenicSpot(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ScenicSpot kitchen = travelService.findReserve(userId);
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
            redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        int collection = 0;//是否收藏过此景区  0没有  1已收藏
        if (kitchenMap != null && kitchenMap.size() > 0) {
            //验证是否收藏过
            boolean flag = travelService.findWhether(CommonUtils.getMyId(), userId);
            if (flag) {
                collection = 1;//1已收藏
            }
            //判断当前用户审核状态是否为3已被其他用户入驻，并更改其删除状态为2管理员删除
            ScenicSpot ik = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
            if (ik != null && ik.getAuditType() == 3) {
                ik.setDeleteType(2);
                travelService.updateDel(ik);
                //清除缓存
                redisUtils.expire(Constants.REDIS_KEY_TRAVEL + userId, 0);
                return returnData(StatusCode.CODE_TRAVEL_OCCUPY.CODE_VALUE, "您当前的店铺已被其他用户入驻，系统已将您目前的店铺删除，如有疑问请及时联系官方客服！", new JSONObject());
            }
        }
        kitchenMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询景区信息(收藏列表)
     * @param id 景区ID
     * @return
     */
    @Override
    public ReturnData findScenicSpotId(@PathVariable long id) {
        Map<String, Object> kitchenMap = new HashMap<>();
        ScenicSpot kitchen = travelService.findById(id);
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        int collection = 0;//是否收藏过此景区  0没有  1已收藏
        //验证是否收藏过
        boolean flag = travelService.findWhether2(CommonUtils.getMyId(), id);
        if (flag) {
            collection = 1;//1已收藏
        }
        kitchenMap = CommonUtils.objectToMap(kitchen);
        kitchenMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 条件查询景区
     * @param watchVideos 筛选视频：0否 1是
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
    public ReturnData findScenicSpotList(@PathVariable int watchVideos, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ScenicSpot> pageBean = null;
        pageBean = travelService.findKitchenList(CommonUtils.getMyId(), watchVideos, name, province, city, district, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ScenicSpot ik = (ScenicSpot) list.get(i);
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
     * 新增门票
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTickets(@Valid @RequestBody ScenicSpotTickets tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        ScenicSpot kitchen = travelService.findById(tickets.getScenicSpotId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "景区不存在", new JSONObject());
        }
        tickets.setAddTime(new Date());
        travelService.addDishes(tickets);
        List list = null;
        list = travelService.findList(tickets.getScenicSpotId());
        if (list != null && list.size() > 0) {
            ScenicSpotTickets tickets1 = (ScenicSpotTickets) list.get(0);
            if (tickets1 != null) {
                kitchen.setCost(tickets1.getCost());
            }
        } else {
            kitchen.setCost(tickets.getCost());
        }
        travelService.updateKitchen3(kitchen);
        //清除景区缓存
        redisUtils.expire(Constants.REDIS_KEY_TRAVEL + kitchen.getUserId(), 0);
        //清除缓存中的门票信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVELTICKETSLIST + tickets.getScenicSpotId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新门票
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updateTickets(@Valid @RequestBody ScenicSpotTickets tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        ScenicSpot kitchen = travelService.findById(tickets.getScenicSpotId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "景区不存在", new JSONObject());
        }
        travelService.updateDishes(tickets);
        List list = null;
        list = travelService.findList(tickets.getScenicSpotId());
        if (list != null && list.size() > 0) {
            ScenicSpotTickets tickets1 = (ScenicSpotTickets) list.get(0);
            if (tickets1 != null) {
                kitchen.setCost(tickets1.getCost());
            }
        } else {
            kitchen.setCost(tickets.getCost());
        }
        travelService.updateKitchen3(kitchen);
        //清除景区缓存
        redisUtils.expire(Constants.REDIS_KEY_TRAVEL + kitchen.getUserId(), 0);
        //清除缓存中的门票信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVELTICKETSLIST + tickets.getScenicSpotId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @param ids
     * @Description: 删除门票
     * @return:
     */
    @Override
    public ReturnData delTickets(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        ScenicSpotTickets dishes = travelService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //查询数据库
        travelService.delDishes(idss, CommonUtils.getMyId());
        List list = null;
        ScenicSpot kitchen = new ScenicSpot();
        list = travelService.findList(dishes.getScenicSpotId());
        if (list != null && list.size() > 0) {
            ScenicSpotTickets tickets1 = (ScenicSpotTickets) list.get(0);
            kitchen.setCost(tickets1.getCost());
        } else {
            kitchen.setCost(0);
        }
        kitchen.setUserId(dishes.getUserId());
        kitchen.setId(dishes.getScenicSpotId());
        travelService.updateKitchen3(kitchen);
        //清除景区缓存
        redisUtils.expire(Constants.REDIS_KEY_TRAVEL + dishes.getUserId(), 0);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVELTICKETSLIST + dishes.getScenicSpotId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询门票详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findTickets(@PathVariable long id) {
        ScenicSpotTickets reserveData = travelService.disheSdetails(id);
        Map<String, Object> map = new HashMap<>();
        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 分页查询门票列表
     * @param id   景区ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findTicketsList(@PathVariable long id, @PathVariable int page, @PathVariable int count) {
        int collection = 0;//是否收藏过此景区  0没有  1已收藏
        List<ScenicSpotTickets> cartList = null;
        ScenicSpot io = travelService.findById(id);
        if (io == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "景区不存在", new JSONObject());
        }
        //从缓存中获取门票列表
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_TRAVELTICKETSLIST + id);
        if (map == null || map.size() <= 0) {
            //查询数据库
            cartList = travelService.findList(id);
            map.put("data", cartList);
            //更新到缓存
            redisUtils.hmset(Constants.REDIS_KEY_TRAVELTICKETSLIST + id, map, Constants.USER_TIME_OUT);
        }
        //验证是否收藏过
        boolean flag = travelService.findWhether2(CommonUtils.getMyId(), io.getId());
        if (flag) {
            collection = 1;//1已收藏
        }
        map.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, map);
    }

    /***
     * 新增收藏
     * @param collect
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addScenicSpotCollect(@Valid @RequestBody ScenicSpotCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否收藏过
        boolean flag = travelService.findWhether2(collect.getMyId(), collect.getScenicSpotId());
        if (flag) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "您已收藏过此景区", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
//        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + collect.getUserId());
//        if (kitchenMap == null || kitchenMap.size() <= 0) {
//            ScenicSpot kitchen2 = travelService.findReserve(collect.getUserId());
//            if (kitchen2 == null) {
//                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，景区不存在！", new JSONObject());
//            }
//            //放入缓存
//            kitchenMap = CommonUtils.objectToMap(kitchen2);
//            redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
//        }
//        ScenicSpot io = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
        ScenicSpot io = travelService.findById(collect.getScenicSpotId());
        if (io != null) {
            //添加收藏记录
            collect.setTime(new Date());
            if (!CommonUtils.checkFull(io.getPicture())) {
                String[] strings = io.getPicture().split(",");
                collect.setPicture(strings[0]);
            }
            collect.setUserId(io.getUserId());
            collect.setType(io.getType());
            collect.setLevels(io.getLevels());
            collect.setName(io.getScenicSpotName());
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
    public ReturnData findScenicSpotCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ScenicSpotCollection> pageBean;
        pageBean = travelService.findCollectionList(userId, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @param ids
     * @Description: 删除收藏
     * @return:
     */
    @Override
    public ReturnData delScenicSpotCollect(@PathVariable String ids) {
        //查询数据库
        travelService.del(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增景区数据
     * @param kitchenData
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTravelData(@Valid @RequestBody ScenicSpotData kitchenData, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(kitchenData.getName())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        ScenicSpotData reserveData = travelService.findReserveDataId(kitchenData.getUid());
        ScenicSpot reserve = new ScenicSpot();
        reserve.setAddTime(new Date());
        reserve.setAddress(kitchenData.getAddress());
        reserve.setClaimId(kitchenData.getUid());
        reserve.setScenicSpotName(kitchenData.getName());
        reserve.setLat(kitchenData.getLatitude());
        reserve.setLon(kitchenData.getLongitude());
        reserve.setPhone(kitchenData.getPhone());
        reserve.setTotalScore(kitchenData.getOverallRating());
        reserve.setAuditType(0);
//        reserve.setBusinessStatus(1);//景区默认关闭
        if (reserveData == null) {//新增
            //新增景区数据表
            kitchenData.setAddTime(new Date());
            travelService.addReserveData(kitchenData);
            //新增景区表
            travelService.addKitchen(reserve);
        } else {//更新
            if (reserveData.getClaimStatus() == 0) {
                //更新景区数据表
                travelService.updateReserveData(kitchenData);
                //更新景区表
                travelService.updateKitchen(reserve);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询景区数据详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findTravelData(@PathVariable long id) {
        ScenicSpotData reserveData = travelService.findReserveData(id);
        Map<String, Object> map = new HashMap<>();
        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 入驻景区
     * @param kitchenReserve
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData claimTravel(@Valid @RequestBody ScenicSpot kitchenReserve, BindingResult bindingResult) {
        ScenicSpot hotel = travelService.findReserve(CommonUtils.getMyId());
        if (hotel != null) {
            if (hotel.getAuditType() == 0) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺正在审核中，审核通过后才能正常营业，请耐心等待", new JSONObject());
            }
            if (hotel.getAuditType() == 1) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经有自己的店铺了，可以切换其他账号再进行创建或入驻", new JSONObject());
            }
        }
        ScenicSpotData kitchen = travelService.findReserveDataId(kitchenReserve.getClaimId());
        if (kitchen == null || kitchen.getClaimStatus() == 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "入驻景区不存在", new JSONObject());
        }
        //新增景点表
        hotel.setAddTime(new Date());
        hotel.setAddress(kitchen.getAddress());
        hotel.setClaimId(kitchen.getUid());
        hotel.setScenicSpotName(kitchen.getName());
        hotel.setLat(kitchen.getLatitude());
        hotel.setLon(kitchen.getLongitude());
        hotel.setPhone(kitchen.getPhone());
        hotel.setTotalScore(kitchen.getOverallRating());
        hotel.setAuditType(0);
        hotel.setBusinessStatus(1);
        hotel.setClaimStatus(1);
        hotel.setClaimTime(kitchen.getClaimTime());
        hotel.setLicence(kitchenReserve.getLicence());
        hotel.setUserId(CommonUtils.getMyId());
        travelService.addKitchen(hotel);
//        //更新景区数据
//        kitchen.setClaimStatus(1);
//        kitchen.setClaimTime(new Date());
//        kitchen.setUserId(CommonUtils.getMyId());
//        travelService.claimKitchen(kitchen);
//        //更新景区
//        ScenicSpot reserve = new ScenicSpot();
//        reserve.setBusinessStatus(1);
//        reserve.setPhone(kitchen.getPhone());
//        reserve.setLicence(kitchenReserve.getLicence());
//        reserve.setClaimId(kitchen.getUid());
//        reserve.setClaimStatus(1);
//        reserve.setClaimTime(kitchen.getClaimTime());
//        reserve.setUserId(CommonUtils.getMyId());
//        travelService.claimKitchen2(reserve);
//        //清除景区缓存
//        redisUtils.expire(Constants.REDIS_KEY_TRAVEL + reserve.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询景区数据列表
     * @param name    景区名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findTravelDataList(@PathVariable String name, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ScenicSpotData> pageBean = null;
        pageBean = travelService.findReserveDataList(name, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            if (CommonUtils.checkFull(name) && lat > 0) {//距离最近
                for (int i = 0; i < list.size(); i++) {
                    ScenicSpotData ik = (ScenicSpotData) list.get(i);
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(ik.getLongitude(), ik.getLatitude(), lon, lat));
                    ik.setDistance(distance);//距离/m
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }
}
