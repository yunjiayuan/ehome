package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.DoorwayBusinessService;
import com.busi.service.UserAccountSecurityService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 家门口商家
 * @author: ZhaoJiaJie
 * @create: 2020-11-18 17:23:53
 */
@RestController
public class DoorwayBusinessController extends BaseController implements DoorwayBusinessApiController {

    @Autowired
    DoorwayBusinessService travelService;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    @Autowired
    MqUtils mqUtils;

    /***
     * 新增商家
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addDoorwayBusiness(@Valid @RequestBody DoorwayBusiness scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有商家
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_DOORWAYBUSINESS + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            DoorwayBusiness kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_DOORWAYBUSINESS + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        DoorwayBusiness ik = (DoorwayBusiness) CommonUtils.mapToObject(kitchenMap, DoorwayBusiness.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经有自己的店铺了，可以切换其他账号再进行创建", new JSONObject());
        }
        scenicSpot.setAuditType(0);
        scenicSpot.setBusinessStatus(1);//商家默认关闭
        scenicSpot.setAddTime(new Date());
        travelService.addKitchen(scenicSpot);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", scenicSpot.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新商家
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeDoorwayBusiness(@Valid @RequestBody DoorwayBusiness scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(scenicSpot.getBusinessName()) && !CommonUtils.checkFull(scenicSpot.getLicence())) {
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
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESS + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param userId
     * @param id
     * @Description: 删除商家
     * @return:
     */
    @Override
    public ReturnData delDoorwayBusiness(@PathVariable long userId, @PathVariable long id) {
        DoorwayBusiness io = travelService.findById(id);
        if (io != null) {
            io.setDeleteType(1);
            travelService.updateDel(io);
            //同时删除该商家的商品
            travelService.delDoorwayBusiness(userId, id);
            //清除缓存中的商品信息
            redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESSCOMMODITY + id, 0);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESS + userId, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新商家营业状态
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updDoorwayBusinessStatus(@Valid @RequestBody DoorwayBusiness scenicSpot, BindingResult bindingResult) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_DOORWAYBUSINESS + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            DoorwayBusiness kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_DOORWAYBUSINESS + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        DoorwayBusiness ik = (DoorwayBusiness) CommonUtils.mapToObject(kitchenMap, DoorwayBusiness.class);
        if (ik == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "商家不存在！", new JSONObject());
        }
        if (ik.getAuditType() == 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺正在审核中，审核通过后才能正常营业，请耐心等待", new JSONObject());
        }
        if (ik.getAuditType() == 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺审核失败，请重新上传清晰、准确、合法的证照", new JSONObject());
        }
        travelService.updateBusiness(scenicSpot);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESS + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询商家信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findDoorwayBusiness(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_DOORWAYBUSINESS + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            DoorwayBusiness kitchen = travelService.findReserve(userId);
            if (kitchen == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            UserInfo sendInfoCache = null;
            sendInfoCache = userInfoUtils.getUserInfo(userId);
            if (sendInfoCache != null) {
                kitchen.setName(sendInfoCache.getName());
                kitchen.setHead(sendInfoCache.getHead());
                kitchen.setProTypeId(sendInfoCache.getProType());
                kitchen.setHouseNumber(sendInfoCache.getHouseNumber());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen);
            redisUtils.hmset(Constants.REDIS_KEY_DOORWAYBUSINESS + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        int collection = 0;//是否收藏过此商家  0没有  1已收藏
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
     * 条件查询商家
     * @param watchVideos 筛选视频：0否 1是
     * @param type  类型
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
    public ReturnData findDoorwayBusinessList(@PathVariable int watchVideos, @PathVariable int type, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<DoorwayBusiness> pageBean = null;
        pageBean = travelService.findKitchenList(type, CommonUtils.getMyId(), watchVideos, name, province, city, district, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                DoorwayBusiness ik = (DoorwayBusiness) list.get(i);
                int distance = (int) Math.round(CommonUtils.getShortestDistance(ik.getLon(), ik.getLat(), lon, lat));
                ik.setDistance(distance);//距离/m
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
     * 新增商品
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addCommodity(@Valid @RequestBody DoorwayBusinessCommodity tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        DoorwayBusiness kitchen = travelService.findById(tickets.getBusinessId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "商家不存在", new JSONObject());
        }
        tickets.setAddTime(new Date());
        travelService.addDishes(tickets);
        List list = null;
        list = travelService.findList(tickets.getBusinessId());
        if (list != null && list.size() > 0) {
            DoorwayBusinessCommodity tickets1 = (DoorwayBusinessCommodity) list.get(0);
            if (tickets1 != null) {
                kitchen.setCost(tickets1.getCost());
            }
        } else {
            kitchen.setCost(tickets.getCost());
        }
        travelService.updateKitchen3(kitchen);
        //清除商家缓存
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESS + kitchen.getUserId(), 0);
        //清除缓存中的商品信息
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESSCOMMODITY + tickets.getBusinessId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新商品
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updateCommodity(@Valid @RequestBody DoorwayBusinessCommodity tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        DoorwayBusiness kitchen = travelService.findById(tickets.getBusinessId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "商家不存在", new JSONObject());
        }
        travelService.updateDishes(tickets);
        List list = null;
        list = travelService.findList(tickets.getBusinessId());
        if (list != null && list.size() > 0) {
            DoorwayBusinessCommodity tickets1 = (DoorwayBusinessCommodity) list.get(0);
            if (tickets1 != null) {
                kitchen.setCost(tickets1.getCost());
            }
        } else {
            kitchen.setCost(tickets.getCost());
        }
        travelService.updateKitchen3(kitchen);
        //清除商家缓存
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESS + kitchen.getUserId(), 0);
        //清除缓存中的商品信息
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESSCOMMODITY + tickets.getBusinessId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @param ids
     * @Description: 删除商品
     * @return:
     */
    @Override
    public ReturnData delCommodity(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        DoorwayBusinessCommodity dishes = travelService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //查询数据库
        travelService.delDishes(idss, CommonUtils.getMyId());
        List list = null;
        DoorwayBusiness kitchen = new DoorwayBusiness();
        list = travelService.findList(dishes.getBusinessId());
        if (list != null && list.size() > 0) {
            DoorwayBusinessCommodity tickets1 = (DoorwayBusinessCommodity) list.get(0);
            kitchen.setCost(tickets1.getCost());
        } else {
            kitchen.setCost(0);
        }
        kitchen.setUserId(dishes.getUserId());
        kitchen.setId(dishes.getBusinessId());
        travelService.updateKitchen3(kitchen);
        //清除商家缓存
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESS + dishes.getUserId(), 0);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_DOORWAYBUSINESSCOMMODITY + dishes.getBusinessId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询商品详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findCommodity(@PathVariable long id) {
        DoorwayBusinessCommodity reserveData = travelService.disheSdetails(id);
//        Map<String, Object> map = new HashMap<>();
//        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", reserveData);
    }

    /***
     * 分页查询商品列表
     * @param id   商家ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findCommodityList(@PathVariable long id, @PathVariable int page, @PathVariable int count) {
        int collection = 0;//是否收藏过此商家  0没有  1已收藏
        List<DoorwayBusinessCommodity> cartList = null;
        DoorwayBusiness io = travelService.findById(id);
        if (io == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "商家不存在", new JSONObject());
        }
        //从缓存中获取商品列表
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_DOORWAYBUSINESSCOMMODITY + id);
        if (map == null || map.size() <= 0) {
            //查询数据库
            cartList = travelService.findList(id);
            map.put("data", cartList);
            //更新到缓存
            redisUtils.hmset(Constants.REDIS_KEY_DOORWAYBUSINESSCOMMODITY + id, map, Constants.USER_TIME_OUT);
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
    public ReturnData addBusinessCollect(@Valid @RequestBody DoorwayBusinessCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否收藏过
        boolean flag = travelService.findWhether2(collect.getMyId(), collect.getBusinessId());
        if (flag) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "您已收藏过此商家", new JSONObject());
        }
        DoorwayBusiness io = travelService.findById(collect.getBusinessId());
        if (io != null) {
            //添加收藏记录
            collect.setTime(new Date());
            if (!CommonUtils.checkFull(io.getPicture())) {
                String[] strings = io.getPicture().split(",");
                collect.setPicture(strings[0]);
            }
            collect.setOpenType(io.getOpenType());
            collect.setOpenTime(io.getOpenTime());
            collect.setCloseTime(io.getCloseTime());
            collect.setUserId(io.getUserId());
            collect.setType(io.getType());
            collect.setName(io.getBusinessName());
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
    public ReturnData findBusinessCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<DoorwayBusinessCollection> pageBean;
        pageBean = travelService.findCollectionList(userId, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @param ids
     * @Description: 删除收藏
     * @return:
     */
    @Override
    public ReturnData delBusinessCollect(@PathVariable String ids) {
        //查询数据库
        travelService.del(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
