package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.LawyerCircleService;
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
 * @description: 律师圈
 * @author: ZHaoJiaJie
 * @create: 2020-03-03 17:21:20
 */
@RestController
public class LawyerCircleController extends BaseController implements LawyerCircleApiController {
    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    LawyerCircleService lawyerCircleService;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 新增律师
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addLvshi(@Valid @RequestBody LawyerCircle homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_LVSHI + homeHospital.getUserId());
        if (map == null || map.size() <= 0) {
            LawyerCircle hospital = lawyerCircleService.findByUserId(homeHospital.getUserId());
            if (hospital != null) {
                //放入缓存
                map = CommonUtils.objectToMap(hospital);
                redisUtils.hmset(Constants.REDIS_KEY_LVSHI + hospital.getUserId(), map, Constants.USER_TIME_OUT);
            }
        }
        LawyerCircle ik = (LawyerCircle) CommonUtils.mapToObject(map, LawyerCircle.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增律师失败，律师已存在！", new JSONObject());
        }
        homeHospital.setAuditType(1);
        homeHospital.setBusinessStatus(1);//默认关闭
        homeHospital.setAddTime(new Date());

        lawyerCircleService.add(homeHospital);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", homeHospital.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新律师
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeLvshi(@Valid @RequestBody LawyerCircle homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        lawyerCircleService.update(homeHospital);
        if (!CommonUtils.checkFull(homeHospital.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeHospital.getUserId(), homeHospital.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_LVSHI + homeHospital.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新律师营业状态
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updLvshiStatus(@Valid @RequestBody LawyerCircle homeHospital, BindingResult bindingResult) {
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
        lawyerCircleService.updateBusiness(homeHospital);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_LVSHI + homeHospital.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询律师详情
     * @param userId
     * @return
     */
    @Override
    public ReturnData findLvshi(@PathVariable long userId) {
        UserInfo sendInfoCache = null;
        LawyerCircle kitchen = null;
        Map<String, Object> kitchenMap = null;
        //匹配机器人数据
        if (userId >= 13870 && userId <= 53870) {
            Random ra = new Random();
            kitchen = lawyerCircleService.findByUserId(ra.nextInt(19) + 1);
            sendInfoCache = userInfoUtils.getUserInfo(kitchen.getUserId());
            if (sendInfoCache != null) {
                if (CommonUtils.checkFull(kitchen.getHeadCover())) {
                    kitchen.setHeadCover(sendInfoCache.getHead());
                }
                kitchen.setProTypeId(sendInfoCache.getProType());
                kitchen.setHouseNumber(sendInfoCache.getHouseNumber());
            }
            kitchenMap = CommonUtils.objectToMap(kitchen);
        } else {
            //查询缓存 缓存中不存在 查询数据库
            kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_LVSHI + userId);
            if (kitchenMap == null || kitchenMap.size() <= 0) {
                kitchen = lawyerCircleService.findByUserId(userId);
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
                }
                sendInfoCache = userInfoUtils.getUserInfo(userId);
                if (sendInfoCache != null) {
//                if (userId == CommonUtils.getMyId()) {//查看自己时返回的是实名信息
//                    //检测是否实名
//                    Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
//                    if (map == null || map.size() <= 0) {
//                        UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
//                        if (userAccountSecurity != null) {
//                            userAccountSecurity.setRedisStatus(1);//数据库中已有记录
//                            //放到缓存中
//                            map = CommonUtils.objectToMap(userAccountSecurity);
//                            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
//                        }
//                    }
//                    if (map != null || map.size() > 0) {
//                        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
//                        if (userAccountSecurity != null) {
//                            if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) || !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
//                                kitchen.setSex(CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard()));
//                                kitchen.setAge(CommonUtils.getAgeByIdCard(userAccountSecurity.getIdCard()));
//                            }
//                        }
//                    }
//                }
                    if (CommonUtils.checkFull(kitchen.getHeadCover())) {
                        kitchen.setHeadCover(sendInfoCache.getHead());
                    }
                    kitchen.setProTypeId(sendInfoCache.getProType());
                    kitchen.setHouseNumber(sendInfoCache.getHouseNumber());
                }
                kitchenMap = CommonUtils.objectToMap(kitchen);
            }
            //放入缓存
            redisUtils.hmset(Constants.REDIS_KEY_LVSHI + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询律师列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param watchVideos
     * @param department  律师类型
     * @param search    模糊搜索（可以是：律所、律师类型、律师名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findLvshiList(@PathVariable int cityId, @PathVariable int watchVideos,
                                    @PathVariable int department, @PathVariable String search, @PathVariable int province, @PathVariable int city,
                                    @PathVariable int district, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<LawyerCircle> pageBean = null;
        pageBean = lawyerCircleService.findList(cityId, watchVideos, CommonUtils.getMyId(), department, search, province, city, district, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                LawyerCircle ik = (LawyerCircle) list.get(i);

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
     * @param userId
     * @param id
     * @Description: 删除律师
     * @return:
     */
    @Override
    public ReturnData delLvshi(@PathVariable long userId, @PathVariable long id) {
        LawyerCircle io = lawyerCircleService.findByUserId(userId);
        if (io != null) {
            io.setDeleteType(1);
            lawyerCircleService.updateDel(io);
        }
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_LVSHI + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增咨询记录
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addLRecord(@Valid @RequestBody LawyerCircleRecord homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setAddTime(new Date());
        homeHospital.setRefreshTime(new Date());
        lawyerCircleService.addRecord(homeHospital);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新咨询记录
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeLRecord(@Valid @RequestBody LawyerCircleRecord homeHospital, BindingResult
            bindingResult) {
        homeHospital.setState(1);
        homeHospital.setRefreshTime(new Date());
        lawyerCircleService.updateRecord(homeHospital);
        //更新医师帮助人数
        LawyerCircle ho = lawyerCircleService.findByUserId(homeHospital.getLvshiId());
        if (ho != null) {
            ho.setHelpNumber(ho.getHelpNumber() + 1);
            lawyerCircleService.updateNumber(ho);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询咨询记录列表
     * @param haveDoctor  有无建议：0全部 1没有
     * @param identity   身份区分：0用户查 1律师查
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findLRecordList(@PathVariable int haveDoctor, @PathVariable int identity,
                                      @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<LawyerCircleRecord> pageBean = null;
        pageBean = lawyerCircleService.findRecordList(CommonUtils.getMyId(), haveDoctor, identity, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        String users = "";
        List list = null;
        List arrylist = null;
        list = pageBean.getList();
        LawyerCircleRecord fc = null;
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        int len = list.size();
        for (int i = 0; i < list.size(); i++) {
            fc = (LawyerCircleRecord) list.get(i);
            if (fc == null) {
                continue;
            }
            if (fc.getUserId() == CommonUtils.getMyId()) {
                if (i < len - 1) {
                    users += fc.getLvshiId() + ",";
                } else {
                    users += fc.getLvshiId();
                }
            } else {
                if (i < len - 1) {
                    users += fc.getUserId() + ",";
                } else {
                    users += fc.getUserId();
                }
            }
        }
        if (identity == 0) {//用户查记录
            arrylist = lawyerCircleService.findUsersList(users.split(","));
            if (arrylist != null && arrylist.size() > 0) {
                for (int j = 0; j < arrylist.size(); j++) {
                    LawyerCircle hospital = (LawyerCircle) arrylist.get(j);
                    if (hospital != null) {
                        for (int i = 0; i < len; i++) {
                            fc = (LawyerCircleRecord) list.get(i);
                            if (fc.getLvshiId() != hospital.getUserId()) {
                                continue;
                            }
                            //检测是否实名
                            Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + fc.getLvshiId());
                            if (map == null || map.size() <= 0) {
                                UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(fc.getLvshiId());
                                if (userAccountSecurity != null) {
                                    userAccountSecurity.setRedisStatus(1);//数据库中已有记录
                                    //放到缓存中
                                    map = CommonUtils.objectToMap(userAccountSecurity);
                                    redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + fc.getLvshiId(), map, Constants.USER_TIME_OUT);
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
                            sendInfoCache = userInfoUtils.getUserInfo(fc.getLvshiId());
                            if (sendInfoCache != null) {
                                fc.setProTypeId(sendInfoCache.getProType());
                                fc.setHouseNumber(sendInfoCache.getHouseNumber());
                            }
                            fc.setHead(hospital.getHeadCover());
                            fc.setName(hospital.getLvshiName());
                        }
                    }
                }
            }
        } else {//医师查记录
            for (int i = 0; i < len; i++) {
                fc = (LawyerCircleRecord) list.get(i);
                if (fc == null) {
                    continue;
                }
                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(fc.getUserId());
                if (sendInfoCache == null) {
                    continue;
                }
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
     * @param id
     * @Description: 删除咨询记录
     * @return:
     */
    @Override
    public ReturnData delLRecord(@PathVariable long id) {
        lawyerCircleService.delRecord(id, CommonUtils.getMyId());

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
