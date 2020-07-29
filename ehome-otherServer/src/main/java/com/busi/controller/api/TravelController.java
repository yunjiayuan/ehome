package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
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
    TravelService kitchenBookedService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

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
            ScenicSpot kitchen2 = kitchenBookedService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ScenicSpot ik = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "入驻景区失败，景区已存在！", new JSONObject());
        }
        scenicSpot.setAuditType(1);
        scenicSpot.setBusinessStatus(1);//景区默认关闭
        scenicSpot.setAddTime(new Date());
        kitchenBookedService.addKitchen(scenicSpot);
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
        if (!CommonUtils.checkFull(scenicSpot.getLicence())) {//上传景区证照
            kitchenBookedService.updateKitchen2(scenicSpot);
        } else {
            kitchenBookedService.updateKitchen(scenicSpot);
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
        ScenicSpot io = kitchenBookedService.findById(id);
        if (io != null) {
            io.setDeleteType(1);
            kitchenBookedService.updateDel(io);
            //同时删除该景区的门票
            kitchenBookedService.delScenicSpot(userId, id);
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
        //判断该景区是否有证照
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ScenicSpot kitchen2 = kitchenBookedService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ScenicSpot ik = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
        if (ik == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "景区不存在！", new JSONObject());
        }
        if (CommonUtils.checkFull(ik.getLicence())) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "该景区未上传景区证照", new JSONObject());
        }
        kitchenBookedService.updateBusiness(scenicSpot);
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
            ScenicSpot kitchen = kitchenBookedService.findReserve(userId);
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
        pageBean = kitchenBookedService.findKitchenList(CommonUtils.getMyId(), watchVideos, name, province, city, district, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
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
        tickets.setAddTime(new Date());
        kitchenBookedService.addDishes(tickets);
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
        kitchenBookedService.updateDishes(tickets);
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
        ScenicSpotTickets dishes = kitchenBookedService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //清除缓存中的菜品信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVELTICKETSLIST + dishes.getScenicSpotId(), 0);
        //查询数据库
        kitchenBookedService.delDishes(idss, CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
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
        List<ScenicSpotTickets> cartList = new ArrayList<>();
        //从缓存中获取门票列表
        cartList = redisUtils.getList(Constants.REDIS_KEY_TRAVELTICKETSLIST + id, 0, -1);
        if (cartList == null || cartList.size() <= 0) {
            //查询数据库
            cartList = kitchenBookedService.findList(id);
            if (cartList != null && cartList.size() > 0) {
                //更新到缓存
                redisUtils.pushList(Constants.REDIS_KEY_TRAVELTICKETSLIST + id, cartList);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, cartList);
    }
}
