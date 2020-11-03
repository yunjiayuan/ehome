package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.PharmacyService;
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
 * @description: 药店相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-10 15:09:01
 */
@RestController
public class PharmacyController extends BaseController implements PharmacyApiController {
    @Autowired
    PharmacyService travelService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    /***
     * 新增药店
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPharmacy(@Valid @RequestBody Pharmacy scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有药店
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PHARMACY + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Pharmacy kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_PHARMACY + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        Pharmacy ik = (Pharmacy) CommonUtils.mapToObject(kitchenMap, Pharmacy.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经有自己的店铺了，可以切换其他账号再进行创建或入驻", new JSONObject());
        }
        scenicSpot.setAuditType(0);
        scenicSpot.setBusinessStatus(1);//药店默认关闭
        scenicSpot.setClaimStatus(1);//默认自己新增为已入驻
        scenicSpot.setAddTime(new Date());
        travelService.addKitchen(scenicSpot);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", scenicSpot.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新药店
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changePharmacy(@Valid @RequestBody Pharmacy scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(scenicSpot.getPharmacyName()) && !CommonUtils.checkFull(scenicSpot.getLicence())) {//上传药店证照
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
        redisUtils.expire(Constants.REDIS_KEY_PHARMACY + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param userId
     * @param id
     * @Description: 删除药店
     * @return:
     */
    @Override
    public ReturnData delPharmacy(@PathVariable long userId, @PathVariable long id) {
        Pharmacy io = travelService.findById(id);
        if (io != null) {
            io.setDeleteType(1);
            travelService.updateDel(io);
            //同时删除该药店的药品
            travelService.delPharmacy(userId, id);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_PHARMACY + userId, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新药店营业状态
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updPharmacyStatus(@Valid @RequestBody Pharmacy scenicSpot, BindingResult bindingResult) {
        //判断该药店是否有证照
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PHARMACY + scenicSpot.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Pharmacy kitchen2 = travelService.findReserve(scenicSpot.getUserId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_PHARMACY + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        Pharmacy ik = (Pharmacy) CommonUtils.mapToObject(kitchenMap, Pharmacy.class);
        if (ik == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "药店不存在！", new JSONObject());
        }
        if (ik.getAuditType() == 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺正在审核中，审核通过后才能正常营业，请耐心等待", new JSONObject());
        }
        if (ik.getAuditType() == 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺审核失败，请重新上传清晰、准确、合法的证照", new JSONObject());
        }
        travelService.updateBusiness(scenicSpot);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_PHARMACY + scenicSpot.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询药店信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findPharmacy(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PHARMACY + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Pharmacy kitchen = travelService.findReserve(userId);
            if (kitchen == null) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "success", new JSONObject());
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
            redisUtils.hmset(Constants.REDIS_KEY_PHARMACY + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        int collection = 0;//是否收藏过此药店  0没有  1已收藏
        if (kitchenMap != null && kitchenMap.size() > 0) {
            //验证是否收藏过
            boolean flag = travelService.findWhether(CommonUtils.getMyId(), userId);
            if (flag) {
                collection = 1;//1已收藏
            }
            //判断当前用户审核状态是否为3已被其他用户入驻，并更改其删除状态为2管理员删除
            Pharmacy ik = (Pharmacy) CommonUtils.mapToObject(kitchenMap, Pharmacy.class);
            if (ik != null && ik.getAuditType() == 3) {
                ik.setDeleteType(2);
                travelService.updateDel(ik);
                //清除缓存
                redisUtils.expire(Constants.REDIS_KEY_PHARMACY + userId, 0);
                return returnData(StatusCode.CODE_TRAVEL_OCCUPY.CODE_VALUE, "您当前的店铺已被其他用户入驻，系统已将您目前的店铺删除，如有疑问请及时联系官方客服！", new JSONObject());
            }
        }
        kitchenMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询药店信息(收藏列表)
     * @param id
     * @return
     */
    @Override
    public ReturnData findPharmacyId(@PathVariable long id) {
        Map<String, Object> kitchenMap = new HashMap<>();
        Pharmacy kitchen = travelService.findById(id);
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        int collection = 0;//是否收藏过此药店  0没有  1已收藏
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
     * 条件查询药店
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
    public ReturnData findPharmacyList(@PathVariable int watchVideos, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<Pharmacy> pageBean = null;
        pageBean = travelService.findKitchenList(CommonUtils.getMyId(), watchVideos, name, province, city, district, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Pharmacy ik = (Pharmacy) list.get(i);
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
     * 新增药品
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPharmacyDrugs(@Valid @RequestBody PharmacyDrugs tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Pharmacy kitchen = travelService.findById(tickets.getPharmacyId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "药店不存在", new JSONObject());
        }
        tickets.setAddTime(new Date());
        travelService.addDishes(tickets);
        List list = null;
        list = travelService.findList(tickets.getPharmacyId());
        if (list != null && list.size() > 0) {
            PharmacyDrugs tickets1 = (PharmacyDrugs) list.get(0);
            if (tickets1 != null) {
                kitchen.setCost(tickets1.getCost());
            }
        } else {
            kitchen.setCost(tickets.getCost());
        }
        travelService.updateKitchen3(kitchen);
        //清除药店缓存
        redisUtils.expire(Constants.REDIS_KEY_PHARMACY + kitchen.getUserId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新药品
     * @param tickets
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updatePharmacyDrugs(@Valid @RequestBody PharmacyDrugs tickets, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Pharmacy kitchen = travelService.findById(tickets.getPharmacyId());
        if (kitchen == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "药店不存在", new JSONObject());
        }
        travelService.updateDishes(tickets);
        List list = null;
        list = travelService.findList(tickets.getPharmacyId());
        if (list != null && list.size() > 0) {
            PharmacyDrugs tickets1 = (PharmacyDrugs) list.get(0);
            if (tickets1 != null) {
                if (tickets.getCost() < tickets1.getCost()) {
                    kitchen.setCost(tickets1.getCost());
                }
            }
        } else {
            kitchen.setCost(tickets.getCost());
        }
        travelService.updateKitchen3(kitchen);
        //清除药店缓存
        redisUtils.expire(Constants.REDIS_KEY_PHARMACY + kitchen.getUserId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", tickets.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @param ids
     * @Description: 删除药品
     * @return:
     */
    @Override
    public ReturnData delPharmacyDrugs(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        PharmacyDrugs dishes = travelService.disheSdetails(id);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //查询数据库
        travelService.delDishes(idss, CommonUtils.getMyId());
        List list = null;
        Pharmacy kitchen = new Pharmacy();
        list = travelService.findList(dishes.getPharmacyId());
        if (list != null && list.size() > 0) {
            PharmacyDrugs tickets1 = (PharmacyDrugs) list.get(0);
            kitchen.setCost(tickets1.getCost());
        } else {
            kitchen.setCost(0);
        }
        kitchen.setUserId(dishes.getUserId());
        kitchen.setId(dishes.getPharmacyId());
        travelService.updateKitchen3(kitchen);
        //清除药店缓存
        redisUtils.expire(Constants.REDIS_KEY_PHARMACY + dishes.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询药品详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findPharmacyDrugs(@PathVariable long id) {
        PharmacyDrugs reserveData = travelService.disheSdetails(id);
//        Map<String, Object> map = new HashMap<>();
//        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", reserveData);
    }

    /***
     * 分页查询药品列表
     * @param id   药房ID
     * @param natureType   药品性质id  -1不限
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPharmacyDrugsList(@PathVariable long id, @PathVariable int natureType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        Map<String, Object> map = new HashMap<>();
        int collection = 0;//是否收藏过此药店  0没有  1已收藏
        List<PharmacyDrugs> cartList = null;
        PageBean<PharmacyDrugs> pageBean = null;
        pageBean = travelService.findList(id, natureType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        cartList = pageBean.getList();
        if (cartList == null || cartList.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        //验证是否收藏过
        PharmacyDrugs tickets = cartList.get(0);
        if (tickets != null) {
            //验证是否收藏过
            boolean flag = travelService.findWhether2(CommonUtils.getMyId(), tickets.getPharmacyId());
            if (flag) {
                collection = 1;//1已收藏
            }
        }
        map.put("data", cartList);
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
    public ReturnData addPharmacyCollect(@Valid @RequestBody PharmacyCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否收藏过
        boolean flag = travelService.findWhether2(collect.getMyId(), collect.getPharmacyId());
        if (flag) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "您已收藏过此药店", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
//        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PHARMACY + collect.getUserId());
//        if (kitchenMap == null || kitchenMap.size() <= 0) {
//            Pharmacy kitchen2 = travelService.findReserve(collect.getUserId());
//            if (kitchen2 == null) {
//                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，药店不存在！", new JSONObject());
//            }
//            //放入缓存
//            kitchenMap = CommonUtils.objectToMap(kitchen2);
//            redisUtils.hmset(Constants.REDIS_KEY_PHARMACY + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
//        }
//        Pharmacy io = (Pharmacy) CommonUtils.mapToObject(kitchenMap, Pharmacy.class);
        Pharmacy io = travelService.findById(collect.getPharmacyId());
        if (io != null) {
            //添加收藏记录
            collect.setTime(new Date());
            if (!CommonUtils.checkFull(io.getPicture())) {
                String[] strings = io.getPicture().split(",");
                collect.setPicture(strings[0]);
            }
            collect.setUserId(io.getUserId());
            collect.setOpenType(io.getOpenType());
            collect.setOpenTime(io.getOpenTime());
            collect.setCloseTime(io.getCloseTime());
            collect.setDistributionMode(io.getDistributionMode());
            collect.setType(io.getType());
            collect.setLevels(io.getLevels());
            collect.setName(io.getPharmacyName());
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
    public ReturnData findPharmacyCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<PharmacyCollection> pageBean;
        pageBean = travelService.findCollectionList(userId, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @param ids
     * @Description: 删除收藏
     * @return:
     */
    @Override
    public ReturnData delPharmacyCollect(@PathVariable String ids) {
        //查询数据库
        travelService.del(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增药店数据
     * @param kitchenData
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPharmacyData(@Valid @RequestBody PharmacyData kitchenData, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(kitchenData.getName())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        PharmacyData reserveData = travelService.findReserveDataId(kitchenData.getUid());
        Pharmacy reserve = new Pharmacy();
        reserve.setAddTime(new Date());
        reserve.setAddress(kitchenData.getAddress());
        reserve.setClaimId(kitchenData.getUid());
        reserve.setPharmacyName(kitchenData.getName());
        reserve.setLat(kitchenData.getLatitude());
        reserve.setLon(kitchenData.getLongitude());
        reserve.setPhone(kitchenData.getPhone());
        reserve.setTotalScore(kitchenData.getOverallRating());
        reserve.setAuditType(0);
//        reserve.setBusinessStatus(1);//药店默认关闭
        if (reserveData == null) {//新增
            //新增药店数据表
            kitchenData.setAddTime(new Date());
            travelService.addReserveData(kitchenData);
            //新增药店表
            travelService.addKitchen(reserve);
        } else {//更新
            if (reserveData.getClaimStatus() == 0) {
                //更新药店数据表
                travelService.updateReserveData(kitchenData);
                //更新药店表
                travelService.updateKitchen(reserve);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询药店数据详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findPharmacyData(@PathVariable long id) {
        PharmacyData reserveData = travelService.findReserveData(id);
//        Map<String, Object> map = new HashMap<>();
//        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", reserveData);
    }

    /***
     * 入驻药店
     * @param kitchenReserve
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData claimPharmacy(@Valid @RequestBody Pharmacy kitchenReserve, BindingResult bindingResult) {
        Pharmacy spot = travelService.findReserve(CommonUtils.getMyId());
        if (spot != null) {
            if (spot.getAuditType() == 0) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的店铺正在审核中，审核通过后才能正常营业，请耐心等待", new JSONObject());
            }
            if (spot.getAuditType() == 1) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经有自己的店铺了，可以切换其他账号再进行创建或入驻", new JSONObject());
            }
        }
        PharmacyData kitchen = travelService.findReserveDataId(kitchenReserve.getClaimId());
        if (kitchen == null || kitchen.getClaimStatus() == 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "入驻药店不存在", new JSONObject());
        }
        //新增药店表
        Pharmacy hotel = new Pharmacy();
        hotel.setAddTime(new Date());
        hotel.setAddress(kitchen.getAddress());
        hotel.setClaimId(kitchen.getUid());
        hotel.setPharmacyName(kitchen.getName());
        hotel.setLat(kitchen.getLatitude());
        hotel.setLon(kitchen.getLongitude());
        hotel.setPhone(kitchen.getPhone());
        hotel.setTotalScore(kitchen.getOverallRating());
        hotel.setAuditType(0);
        hotel.setBusinessStatus(1);
        hotel.setClaimStatus(1);
        hotel.setClaimTime(new Date());
        hotel.setLicence(kitchenReserve.getLicence());
        hotel.setUserId(CommonUtils.getMyId());
        travelService.addKitchen(hotel);
//        //更新药店数据
//        kitchen.setClaimStatus(1);
//        kitchen.setClaimTime(new Date());
//        kitchen.setUserId(CommonUtils.getMyId());
//        travelService.claimKitchen(kitchen);
//        //更新药店
//        Pharmacy reserve = new Pharmacy();
//        reserve.setBusinessStatus(1);
//        reserve.setPhone(kitchen.getPhone());
//        reserve.setLicence(kitchenReserve.getLicence());
//        reserve.setClaimId(kitchen.getUid());
//        reserve.setClaimStatus(1);
//        reserve.setClaimTime(kitchen.getClaimTime());
//        reserve.setUserId(CommonUtils.getMyId());
//        travelService.claimKitchen2(reserve);
//        //清除药店缓存
//        redisUtils.expire(Constants.REDIS_KEY_PHARMACY + reserve.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询药店数据列表
     * @param name    药店名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPharmacyDataList(@PathVariable String name, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<PharmacyData> pageBean = null;
        pageBean = travelService.findReserveDataList(name, lat, lon, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            if (CommonUtils.checkFull(name) && lat > 0) {//距离最近
                for (int i = 0; i < list.size(); i++) {
                    PharmacyData ik = (PharmacyData) list.get(i);
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(ik.getLongitude(), ik.getLatitude(), lon, lat));
                    ik.setDistance(distance);//距离/m
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }
}
