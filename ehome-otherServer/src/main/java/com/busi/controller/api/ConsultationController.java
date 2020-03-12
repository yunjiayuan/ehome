package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ConsultationService;
import com.busi.service.HomeHospitalRecordService;
import com.busi.service.HomeHospitalService;
import com.busi.service.LawyerCircleService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
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
    HomeHospitalService homeHospitalService;

    @Autowired
    LawyerCircleService lawyerCircleService;

    @Autowired
    HomeHospitalRecordService homeHospitalRecordService;

    @Autowired
    private ConsultationService consultationService;

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
            homeHospitalRecordService.add(homeHospital);
        } else {
            LawyerCircleRecord homeHospital = new LawyerCircleRecord();
            homeHospital.setContent(consultationOrders.getContent());
            homeHospital.setLvshiId(consultationOrders.getPeopleId());
            homeHospital.setAddTime(new Date());
            homeHospital.setRefreshTime(new Date());
            homeHospital.setUserId(CommonUtils.getMyId());
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
}
