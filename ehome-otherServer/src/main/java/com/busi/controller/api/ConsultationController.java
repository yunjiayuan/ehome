package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ConsultationService;
import com.busi.service.HomeHospitalRecordService;
import com.busi.service.HomeHospitalService;
import com.busi.service.LawyerCircleService;
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
 * @description: 律师医生咨询相关
 * @author: ZHaoJiaJie
 * @create: 2020-03-12 15:10:25
 */
@RestController
public class ConsultationController extends BaseController implements ConsultationApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HomeHospitalService homeHospitalService;

    @Autowired
    LawyerCircleService lawyerCircleService;

    @Autowired
    HomeHospitalRecordService homeHospitalRecordService;

    @Autowired
    ConsultationService consultationService;

    /***
     * 新增订单
     * @param consultationOrders
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addConsultOrder(@Valid @RequestBody ConsultationOrders consultationOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        int type = consultationOrders.getType();
        if (type < 2) {
            type = 0;
        } else {
            type = 1;
        }
        List<ConsultationFee> list = null;
        if (consultationOrders.getOccupation() == 0) {//医生
            //查询缓存 缓存中不存在 查询数据库
            list = redisUtils.getList(Constants.REDIS_KEY_CONSULTATION + consultationOrders.getOccupation() + "_" + consultationOrders.getTitle() + "_" + type, 0, -1);
            if (list == null || list.size() <= 0) {
                list = consultationService.findList(consultationOrders.getOccupation(), consultationOrders.getTitle(), type);
                //放入缓存
                if (list != null && list.size() > 0) {
                    redisUtils.pushList(Constants.REDIS_KEY_CONSULTATION + consultationOrders.getOccupation() + "_" + consultationOrders.getTitle() + "_" + type, list, Constants.USER_TIME_OUT);
                }
            }
        } else {//律师
            //查询缓存 缓存中不存在 查询数据库
            list = redisUtils.getList(Constants.REDIS_KEY_CONSULTATION + consultationOrders.getOccupation() + "_" + consultationOrders.getTitle() + "_" + type, 0, -1);
            if (list == null || list.size() <= 0) {
                list = consultationService.findList(consultationOrders.getOccupation(), consultationOrders.getTitle(), type);
                //放入缓存
                if (list != null && list.size() > 0) {
                    redisUtils.pushList(Constants.REDIS_KEY_CONSULTATION + consultationOrders.getOccupation() + "_" + consultationOrders.getTitle() + "_" + type, list, Constants.USER_TIME_OUT);
                }
            }
        }
        int duration[] = {5, 15, 30, 60};
        int times = duration[consultationOrders.getDuration()];
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ConsultationFee consultationFee = list.get(i);
                if (consultationFee.getDuration() == times) {
                    consultationOrders.setDuration(times);
                    consultationOrders.setMoney(consultationFee.getCost());
                    break;
                }
            }
        }
        long time = new Date().getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + CommonUtils.getMyId() + random, 16);
        consultationOrders.setOrderNumber(noRandom);
        consultationOrders.setTime(new Date());
        consultationOrders.setUserId(CommonUtils.getMyId());
        //放入缓存 5分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(consultationOrders);
        redisUtils.hmset(Constants.REDIS_KEY_CONSULTATIONORDER + CommonUtils.getMyId() + "_" + consultationOrders.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_5);
        //新增咨询主诉
        if (consultationOrders.getOccupation() == 0) {
            HomeHospitalRecord homeHospital = new HomeHospitalRecord();
            homeHospital.setContent(consultationOrders.getContent());
            homeHospital.setDoctorId(consultationOrders.getPeopleId());
            homeHospital.setAddTime(new Date());
            homeHospital.setRefreshTime(new Date());
            homeHospital.setUserId(CommonUtils.getMyId());
            homeHospital.setOrderNumber(noRandom);
            homeHospital.setTime(new Date());
            homeHospital.setDuration(consultationOrders.getDuration());
            homeHospital.setMoney(consultationOrders.getMoney());
            homeHospital.setType(consultationOrders.getType());
            homeHospital.setConsultationStatus(1);
            homeHospital.setTitle(consultationOrders.getTitle());
            homeHospitalRecordService.add(homeHospital);
        } else {
            LawyerCircleRecord homeHospital = new LawyerCircleRecord();
            homeHospital.setContent(consultationOrders.getContent());
            homeHospital.setLvshiId(consultationOrders.getPeopleId());
            homeHospital.setAddTime(new Date());
            homeHospital.setRefreshTime(new Date());
            homeHospital.setUserId(CommonUtils.getMyId());
            homeHospital.setOrderNumber(noRandom);
            homeHospital.setTime(new Date());
            homeHospital.setDuration(consultationOrders.getDuration());
            homeHospital.setMoney(consultationOrders.getMoney());
            homeHospital.setType(consultationOrders.getType());
            homeHospital.setConsultationStatus(1);
            homeHospital.setTitle(consultationOrders.getTitle());
            lawyerCircleService.addRecord(homeHospital);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", noRandom);
    }

    /***
     * 查询收费信息
     * @param occupation 职业：0医生  1律师
     * @param type     咨询类型：0语音、视频  1图文
     * @param userId   咨询对象ID
     * @return
     */
    @Override
    public ReturnData findConsultList(@PathVariable int occupation, @PathVariable int type, @PathVariable long userId) {
        int title = 0;// 职称：（occupation=0时 0副主任 主任 专家  1其他）  （occupation=1时 0初级律师 1中级律师  2高级律师）
        List<ConsultationFee> list = null;
        if (occupation == 0) {//医生
            //查询缓存 缓存中不存在 查询数据库
            Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_HOMEHOSPITAL + userId);
            if (map == null || map.size() <= 0) {
                HomeHospital hospital = homeHospitalService.findByUserId(userId);
                if (hospital != null) {
                    //放入缓存
                    map = CommonUtils.objectToMap(hospital);
                    redisUtils.hmset(Constants.REDIS_KEY_HOMEHOSPITAL + hospital.getUserId(), map, Constants.USER_TIME_OUT);
                }
            }
            HomeHospital ik = (HomeHospital) CommonUtils.mapToObject(map, HomeHospital.class);
            if (ik == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "success", new JSONObject());
            }
            if (ik.getTitle() < 7 || ik.getTitle() > 12) {
                title = 1;
            }
            //查询缓存 缓存中不存在 查询数据库
            list = redisUtils.getList(Constants.REDIS_KEY_CONSULTATION + occupation + "_" + title + "_" + type, 0, -1);
            if (list == null || list.size() <= 0) {
                list = consultationService.findList(occupation, title, type);
                //放入缓存
                if (list != null && list.size() > 0) {
                    redisUtils.pushList(Constants.REDIS_KEY_CONSULTATION + occupation + "_" + title + "_" + type, list, Constants.USER_TIME_OUT);
                }
            }
        } else {//律师
            //查询缓存 缓存中不存在 查询数据库
            Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_LVSHI + userId);
            if (map == null || map.size() <= 0) {
                LawyerCircle hospital = lawyerCircleService.findByUserId(userId);
                if (hospital != null) {
                    //放入缓存
                    map = CommonUtils.objectToMap(hospital);
                    redisUtils.hmset(Constants.REDIS_KEY_LVSHI + hospital.getUserId(), map, Constants.USER_TIME_OUT);
                }
            }
            LawyerCircle ik = (LawyerCircle) CommonUtils.mapToObject(map, LawyerCircle.class);
            if (ik == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "success", new JSONObject());
            }
            title = ik.getTitle();
            //查询缓存 缓存中不存在 查询数据库
            list = redisUtils.getList(Constants.REDIS_KEY_CONSULTATION + occupation + "_" + title + "_" + type, 0, -1);
            if (list == null || list.size() <= 0) {
                list = consultationService.findList(occupation, title, type);
                //放入缓存
                if (list != null && list.size() > 0) {
                    redisUtils.pushList(Constants.REDIS_KEY_CONSULTATION + occupation + "_" + title + "_" + type, list, Constants.USER_TIME_OUT);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

    /***
     * 更新咨询状态
     * @param type   更新类型：1咨询中 2已咨询
     * @param occupation 职业：0医生  1律师
     * @param id   订单编号
     * @return
     */
    @Override
    public ReturnData upConsultationStatus(@PathVariable int type, @PathVariable int occupation, @PathVariable String id) {
        if (occupation == 0) {//职业：0医生
            HomeHospitalRecord record = new HomeHospitalRecord();
            record.setOrderNumber(id);
            record.setConsultationStatus(type);
            homeHospitalRecordService.upConsultationStatus(record);
        } else {//职业： 1律师
            LawyerCircleRecord record = new LawyerCircleRecord();
            record.setOrderNumber(id);
            record.setConsultationStatus(type);
            lawyerCircleService.upConsultationStatus(record);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新咨询时长
     * @param occupation 职业：0医生  1律师
     * @param id   订单编号
     * @param duration   咨询时长
     * @return
     */
    @Override
    public ReturnData upActualDuration(@PathVariable int occupation, @PathVariable String id, @PathVariable int duration) {
        if (occupation == 0) {//职业：0医生
            HomeHospitalRecord record = new HomeHospitalRecord();
            record.setOrderNumber(id);
            record.setActualDuration(duration);
            homeHospitalRecordService.upActualDuration(record);
        } else {//职业： 1律师
            LawyerCircleRecord record = new LawyerCircleRecord();
            record.setOrderNumber(id);
            record.setActualDuration(duration);
            lawyerCircleService.upActualDuration(record);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询等待人员列表(默认第一位是正在咨询中，其余为等待中)
     * @param occupation 职业：0医生  1律师
     * @param userId   医师或律师ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findWaitList(@PathVariable int occupation, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        List list = null;
        if (occupation == 0) {//职业：0医生  1律师
            PageBean<HomeHospitalRecord> pageBean = null;
            pageBean = homeHospitalService.findWaitList(userId, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            list = pageBean.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HomeHospitalRecord ik = (HomeHospitalRecord) list.get(i);
                    UserInfo sendInfoCache = null;
                    sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                    if (sendInfoCache != null) {
                        ik.setName(sendInfoCache.getName());
                        ik.setHead(sendInfoCache.getHead());
                    }
                }
            }
        } else {
            PageBean<LawyerCircleRecord> pageBean = null;
            pageBean = lawyerCircleService.findWaitList(userId, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            list = pageBean.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    LawyerCircleRecord ik = (LawyerCircleRecord) list.get(i);
                    UserInfo sendInfoCache = null;
                    sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                    if (sendInfoCache != null) {
                        ik.setName(sendInfoCache.getName());
                        ik.setHead(sendInfoCache.getHead());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询等待咨询人数
     * @param occupation 职业：0医生  1律师
     * @param userId   医师或律师ID
     * @return
     */
    @Override
    public ReturnData findWaitNum(@PathVariable int occupation, @PathVariable long userId) {
        //开始查询
        List list = null;
        int number = 0;
        if (occupation == 0) {//职业：0医生  1律师
            PageBean<HomeHospitalRecord> pageBean = null;
            pageBean = homeHospitalService.findWaitList(userId, 1, 10000);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            list = pageBean.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HomeHospitalRecord ik = (HomeHospitalRecord) list.get(i);
                    if (ik.getConsultationStatus() == 1) {//咨询中
                        if (ik.getUserId() != CommonUtils.getMyId()) {//不是自己
                            number += 1;
                        } else {//自己
                            number = 0;
                            break;
                        }
                    } else {//等待中
                        if (ik.getUserId() != CommonUtils.getMyId()) {//不是自己
                            number += 1;
                        } else {//自己
                            break;
                        }
                    }
                }
            }
        } else {
            PageBean<LawyerCircleRecord> pageBean = null;
            pageBean = lawyerCircleService.findWaitList(userId, 1, 10000);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            list = pageBean.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HomeHospitalRecord ik = (HomeHospitalRecord) list.get(i);
                    if (ik.getConsultationStatus() == 1) {//咨询中
                        if (ik.getUserId() != CommonUtils.getMyId()) {//不是自己
                            number += 1;
                        } else {//自己
                            number = 0;
                            break;
                        }
                    } else {//等待中
                        if (ik.getUserId() != CommonUtils.getMyId()) {//不是自己
                            number += 1;
                        } else {//自己
                            break;
                        }
                    }
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("number", number);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
