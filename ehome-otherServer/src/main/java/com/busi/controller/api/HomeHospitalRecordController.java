package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeHospitalRecordService;
import com.busi.service.HomeHospitalService;
import com.busi.service.UserAccountSecurityService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 家医馆咨询
 * @author: ZHaoJiaJie
 * @create: 2020-01-07 14:42
 */
@RestController
public class HomeHospitalRecordController extends BaseController implements HomeHospitalRecordApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HomeHospitalService homeHospitalService;

    @Autowired
    HomeHospitalRecordService homeHospitalRecordService;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 新增
     * @param homeHospital
     * @return
     */
    @Override
    public ReturnData addHRecord(@Valid @RequestBody HomeHospitalRecord homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setAddTime(new Date());
        homeHospital.setRefreshTime(new Date());
        homeHospitalRecordService.add(homeHospital);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新医嘱
     * @param homeHospital
     * @return
     */
    @Override
    public ReturnData changeHRecord(@Valid @RequestBody HomeHospitalRecord homeHospital, BindingResult bindingResult) {
        homeHospital.setState(1);
        homeHospital.setRefreshTime(new Date());
        homeHospitalRecordService.update(homeHospital);
        //更新医师帮助人数
        HomeHospital ho = homeHospitalService.findByUserId(homeHospital.getDoctorId());
        if (ho != null) {
            ho.setHelpNumber(ho.getHelpNumber() + 1);
            homeHospitalService.updateNumber(ho);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询列表
     * @param haveDoctor  有无医嘱：0全部 1没有
     * @param identity   身份区分：0用户查 1医师查
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHRecordList(@PathVariable int haveDoctor, @PathVariable int identity, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<HomeHospitalRecord> pageBean = null;
        pageBean = homeHospitalRecordService.findList(CommonUtils.getMyId(), haveDoctor, identity, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        String users = "";
        List list = null;
        List arrylist = null;
        list = pageBean.getList();
        HomeHospitalRecord fc = null;
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        int len = list.size();
        for (int i = 0; i < list.size(); i++) {
            fc = (HomeHospitalRecord) list.get(i);
            if (fc == null) {
                continue;
            }
//            if (fc.getUserId() == CommonUtils.getMyId()) {
            if (i < len - 1) {
                users += fc.getDoctorId() + ",";
            } else {
                users += fc.getDoctorId();
            }
//            }
//            else {
//                if (i < len - 1) {
//                    users += fc.getUserId() + ",";
//                } else {
//                    users += fc.getUserId();
//                }
//            }
        }
        if (identity == 0) {//用户查记录
            arrylist = homeHospitalService.findUsersList(users.split(","));
            if (arrylist != null && arrylist.size() > 0) {
                for (int j = 0; j < arrylist.size(); j++) {
                    HomeHospital hospital = (HomeHospital) arrylist.get(j);
                    if (hospital != null) {
                        for (int i = 0; i < len; i++) {
                            fc = (HomeHospitalRecord) list.get(i);
                            if (fc.getDoctorId() != hospital.getUserId()) {
                                continue;
                            }
                            //检测是否实名
                            Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + fc.getDoctorId());
                            if (map == null || map.size() <= 0) {
                                UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(fc.getDoctorId());
                                if (userAccountSecurity != null) {
                                    userAccountSecurity.setRedisStatus(1);//数据库中已有记录
                                    //放到缓存中
                                    map = CommonUtils.objectToMap(userAccountSecurity);
                                    redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + fc.getDoctorId(), map, Constants.USER_TIME_OUT);
                                }
                            }
                            if (map != null || map.size() > 0) {
                                UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
                                if (userAccountSecurity != null) {
                                    if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) || !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                                        fc.setSex(CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard()));
                                        fc.setAge(CommonUtils.getAgeByIdCard(userAccountSecurity.getIdCard()));
                                    }
                                }
                            }
                            UserInfo sendInfoCache = null;
                            sendInfoCache = userInfoUtils.getUserInfo(fc.getDoctorId());
                            if (sendInfoCache != null) {
                                fc.setProTypeId(sendInfoCache.getProType());
                                fc.setHouseNumber(sendInfoCache.getHouseNumber());
                                if (!CommonUtils.checkFull(hospital.getHeadCover())) {
                                    fc.setHead(hospital.getHeadCover());
                                } else {
                                    fc.setHead(sendInfoCache.getHead());
                                }
                            }
                            fc.setName(hospital.getPhysicianName());
                        }
                    }
                }
            }
        } else {//医师查记录
            //查询医生缓存 缓存中不存在 查询数据库
            Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_HOMEHOSPITAL + CommonUtils.getMyId());
            if (map == null || map.size() <= 0) {
                HomeHospital hospital = homeHospitalService.findByUserId(CommonUtils.getMyId());
                if (hospital != null) {
                    //放入缓存
                    map = CommonUtils.objectToMap(hospital);
                    redisUtils.hmset(Constants.REDIS_KEY_HOMEHOSPITAL + hospital.getUserId(), map, Constants.USER_TIME_OUT);
                }
            }
            HomeHospital hospital = (HomeHospital) CommonUtils.mapToObject(map, HomeHospital.class);
            if (hospital == null) {
                list = null;
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
            }
            UserInfo userInfo = null;
            userInfo = userInfoUtils.getUserInfo(hospital.getUserId());
            for (int i = 0; i < len; i++) {
                fc = (HomeHospitalRecord) list.get(i);
                if (fc == null) {
                    continue;
                }
                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(fc.getUserId());
                if (sendInfoCache == null) {
                    list.remove(i);
                    continue;
                }
                if (!CommonUtils.checkFull(hospital.getHeadCover())) {
                    fc.setDoctorHead(hospital.getHeadCover());
                } else {
                    fc.setDoctorHead(userInfo.getHead());
                }
                fc.setDoctorName(hospital.getPhysicianName());
                fc.setProTypeId(sendInfoCache.getProType());
                fc.setHouseNumber(sendInfoCache.getHouseNumber());
                fc.setSex(sendInfoCache.getSex());
                fc.setHead(sendInfoCache.getHead());
                fc.setName(sendInfoCache.getName());
                fc.setAge(getAge(sendInfoCache.getBirthday()));
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delHRecord(@PathVariable long id) {
        homeHospitalRecordService.del(id, CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
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
