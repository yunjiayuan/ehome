package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeHospitalService;
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
 * @description: 家医馆
 * @author: ZHaoJiaJie
 * @create: 2020-01-07 14:19
 */
@RestController
public class HomeHospitalController extends BaseController implements HomeHospitalApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HomeHospitalService homeHospitalService;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 新增
     * @param homeHospital
     * @return
     */
    @Override
    public ReturnData addHospital(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_HOMEHOSPITAL + homeHospital.getUserId());
        if (map == null || map.size() <= 0) {
            HomeHospital hospital = homeHospitalService.findByUserId(homeHospital.getUserId());
            if (hospital != null) {
                //放入缓存
                map = CommonUtils.objectToMap(hospital);
                redisUtils.hmset(Constants.REDIS_KEY_HOMEHOSPITAL + hospital.getUserId(), map, Constants.USER_TIME_OUT);
            }
        }
        HomeHospital ik = (HomeHospital) CommonUtils.mapToObject(map, HomeHospital.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增医馆失败，医馆已存在！", new JSONObject());
        }
//        homeHospital.setAuditType(1);
        homeHospital.setBusinessStatus(1);//默认关闭
        homeHospital.setAddTime(new Date());

        homeHospitalService.add(homeHospital);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", homeHospital.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新
     * @param homeHospital
     * @return
     */
    @Override
    public ReturnData changeHospital(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospitalService.update(homeHospital);
        if (!CommonUtils.checkFull(homeHospital.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeHospital.getUserId(), homeHospital.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOMEHOSPITAL + homeHospital.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 身份认证
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData identityAuthentication(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult) {
        HomeHospital hospital = homeHospitalService.findByUserId(homeHospital.getUserId());
        if (hospital == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "认证失败", new JSONObject());
        }
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + homeHospital.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(homeHospital.getUserId());
            if (userAccountSecurity == null) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + homeHospital.getUserId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        homeHospital.setPhysicianName(userAccountSecurity.getRealName());
        Map<String, String> map2 = getBirAgeSex(userAccountSecurity.getIdCard());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(map2.get("birthday"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        homeHospital.setAge(date);
        homeHospital.setSex(CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard()));
        homeHospitalService.update2(homeHospital);
        if (!CommonUtils.checkFull(homeHospital.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeHospital.getUserId(), homeHospital.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOMEHOSPITAL + homeHospital.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新营业状态
     * @param homeHospital
     * @return
     */
    @Override
    public ReturnData updHospitalStatus(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult) {
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + homeHospital.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(homeHospital.getUserId());
            if (userAccountSecurity == null) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + homeHospital.getUserId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        homeHospitalService.updateBusiness(homeHospital);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_HOMEHOSPITAL + homeHospital.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询详情
     * @param userId
     * @return
     */
    @Override
    public ReturnData findHospital(@PathVariable long userId) {
        UserInfo sendInfoCache = null;
        HomeHospital kitchen = null;
        Map<String, Object> kitchenMap = null;
        //匹配机器人数据
        if (userId >= 13870 && userId <= 53870) {
            HomeHospital hospital = new HomeHospital();
            Random ra = new Random();
//            kitchen = homeHospitalService.findByUserId(ra.nextInt(37) + 1);
            sendInfoCache = userInfoUtils.getUserInfo(userId);
            if (sendInfoCache != null) {
                hospital.setUserId(userId);
                String num = ra.nextLong() + "";
                hospital.setPracticeNumber(Long.parseLong(num.substring(num.length() - 17)));
                hospital.setPhysicianName(sendInfoCache.getName());
                hospital.setSex(sendInfoCache.getSex());
                hospital.setAge(sendInfoCache.getBirthday());
                hospital.setProvince(sendInfoCache.getProvince());
                hospital.setCity(sendInfoCache.getCity());
                hospital.setDistrict(sendInfoCache.getDistrict());
                hospital.setHeadCover(sendInfoCache.getHead());
                hospital.setProTypeId(sendInfoCache.getProType());
                hospital.setHouseNumber(sendInfoCache.getHouseNumber());
            }
            kitchenMap = CommonUtils.objectToMap(hospital);
        } else {
            //查询缓存 缓存中不存在 查询数据库
            kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOMEHOSPITAL + userId);
            if (kitchenMap == null || kitchenMap.size() <= 0) {
                kitchen = homeHospitalService.findByUserId(userId);
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
                }
                sendInfoCache = userInfoUtils.getUserInfo(userId);
                if (sendInfoCache != null) {
                    if (CommonUtils.checkFull(kitchen.getHeadCover())) {
                        kitchen.setHeadCover(sendInfoCache.getHead());
                    }
                    kitchen.setProTypeId(sendInfoCache.getProType());
                    kitchen.setHouseNumber(sendInfoCache.getHouseNumber());
                }
                kitchenMap = CommonUtils.objectToMap(kitchen);
            }
            //放入缓存
            redisUtils.hmset(Constants.REDIS_KEY_HOMEHOSPITAL + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param watchVideos 筛选视频：0否 1是
     * @param department  科室
     * @param search    模糊搜索（可以是：症状、疾病、医院、科室、医生名字）
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
    public ReturnData findHospitalList(@PathVariable int cityId, @PathVariable int watchVideos, @PathVariable int department, @PathVariable String search, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeHospital> pageBean = null;
        pageBean = homeHospitalService.findList(cityId, watchVideos, CommonUtils.getMyId(), department, search, province, city, district, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HomeHospital ik = (HomeHospital) list.get(i);
                int distance = (int) Math.round(CommonUtils.getShortestDistance(ik.getLongitude(), ik.getLatitude(), lon, lat));
                ik.setDistance(distance);//距离/m
                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                if (sendInfoCache != null) {
                    if (CommonUtils.checkFull(ik.getHeadCover())) {
                        ik.setHeadCover(sendInfoCache.getHead());
                    }
                    ik.setProTypeId(sendInfoCache.getProType());
                    ik.setHouseNumber(sendInfoCache.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delHospital(@PathVariable long userId, @PathVariable long id) {
        HomeHospital io = homeHospitalService.findByUserId(userId);
        if (io != null) {
            io.setDeleteType(1);
            homeHospitalService.updateDel(io);
        }
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_HOMEHOSPITAL + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 通过身份证号码获取出生日期、性别、年龄
     * @param certificateNo  身份证号
     * @return 返回的出生日期格式：1990-01-01   性别格式：F-女，M-男
     */
    public static Map<String, String> getBirAgeSex(String certificateNo) {
        String birthday = "";
        String age = "";
        String sexCode = "";

        int year = Calendar.getInstance().get(Calendar.YEAR);
        char[] number = certificateNo.toCharArray();
        boolean flag = true;
        if (number.length == 15) {
            for (int x = 0; x < number.length; x++) {
                if (!flag) return new HashMap<String, String>();
                flag = Character.isDigit(number[x]);
            }
        } else if (number.length == 18) {
            for (int x = 0; x < number.length - 1; x++) {
                if (!flag) return new HashMap<String, String>();
                flag = Character.isDigit(number[x]);
            }
        }
        if (flag && certificateNo.length() == 15) {
            birthday = "19" + certificateNo.substring(6, 8) + "-"
                    + certificateNo.substring(8, 10) + "-"
                    + certificateNo.substring(10, 12);
            sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 3, certificateNo.length())) % 2 == 0 ? "F" : "M";
            age = (year - Integer.parseInt("19" + certificateNo.substring(6, 8))) + "";
        } else if (flag && certificateNo.length() == 18) {
            birthday = certificateNo.substring(6, 10) + "-"
                    + certificateNo.substring(10, 12) + "-"
                    + certificateNo.substring(12, 14);
            sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 4, certificateNo.length() - 1)) % 2 == 0 ? "F" : "M";
            age = (year - Integer.parseInt(certificateNo.substring(6, 10))) + "";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("birthday", birthday);
        map.put("age", age);
        map.put("sexCode", sexCode);
        return map;
    }
}
