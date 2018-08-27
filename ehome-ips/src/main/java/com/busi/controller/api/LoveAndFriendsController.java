package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.DetailedUserInfoService;
import com.busi.service.LoveAndFriendsService;
import com.busi.service.UserInfoService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @program: 婚恋交友
 * @author: ZHaoJiaJie
 * @create: 2018-08-02 13:39
 */
@RestController
public class LoveAndFriendsController extends BaseController implements LoveAndFriendsApiController {

    @Autowired
    LoveAndFriendsService loveAndFriendsService;

    @Autowired
    DetailedUserInfoService detailedUserInfoService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    /***
     * 新增
     * @param loveAndFriends
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库（是否已发布过）
        Map<String, Object> loveAndFriendsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + loveAndFriends.getUserId());
        if (loveAndFriendsMap == null || loveAndFriendsMap.size() <= 0) {
            LoveAndFriends andFriends;
            andFriends = loveAndFriendsService.findByIdUser(loveAndFriends.getUserId());
            if (andFriends == null) {
                //符合推荐规则 添加到缓存home列表中
                int num3 = 0;//图片
                int fraction = 0;//公告分数
                int num = CommonUtils.getStringLengsByByte(loveAndFriends.getTitle());//标题
                int num2 = CommonUtils.getStringLengsByByte(loveAndFriends.getContent());//内容
                if (!CommonUtils.checkFull(loveAndFriends.getImgUrl())) {
                    String[] imgArray = loveAndFriends.getImgUrl().split(",");
                    if (imgArray != null) {
                        num3 = imgArray.length;

                        if (num3 >= 6) {
                            fraction += 40;
                        }
                        if (num3 == 1) {
                            fraction += 15;
                        }
                        if (num3 > 3 && num3 < 6) {
                            fraction += 30;
                        }
                    }
                }
                if (num <= 5 * 2) {
                    fraction += 5;
                }
                if (num > 5 * 2 && num <= 10 * 2) {
                    fraction += 10;
                }
                if (num > 10 * 2 && num <= 20 * 2) {
                    fraction += 20;
                }
                if (num > 20 * 2) {
                    fraction += 30;
                }

                if (num2 <= 20 * 2) {
                    fraction += 5;
                }
                if (num2 > 20 * 2 && num2 <= 50 * 2) {
                    fraction += 10;
                }
                if (num2 > 50 * 2 && num2 <= 80 * 2) {
                    fraction += 20;
                }
                if (num2 > 80 * 2) {
                    fraction += 30;
                }

                //新增婚恋交友
                loveAndFriends.setAuditType(2);
                loveAndFriends.setDeleteType(1);
                loveAndFriends.setFraction(fraction);
                loveAndFriends.setRefreshTime(new Date());
                loveAndFriends.setReleaseTime(new Date());
                loveAndFriendsService.add(loveAndFriends);

                //放入缓存
                loveAndFriendsMap = CommonUtils.objectToMap(loveAndFriends);
                redisUtils.hmset(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + CommonUtils.getMyId(), loveAndFriendsMap, Constants.USER_TIME_OUT);
                //新增home
                if (fraction > 70) {
                    IPS_Home ipsHome = new IPS_Home();
                    ipsHome.setInfoId(loveAndFriends.getId());
                    ipsHome.setTitle(loveAndFriends.getTitle());
                    ipsHome.setUserId(loveAndFriends.getUserId());
                    ipsHome.setContent(loveAndFriends.getContent());
                    ipsHome.setMediumImgUrl(loveAndFriends.getImgUrl());
                    ipsHome.setReleaseTime(loveAndFriends.getReleaseTime());
                    ipsHome.setRefreshTime(loveAndFriends.getRefreshTime());
                    ipsHome.setAuditType(2);
                    ipsHome.setDeleteType(1);
                    ipsHome.setAfficheType(1);
                    ipsHome.setFraction(fraction);

                    redisUtils.addList(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, Constants.USER_TIME_OUT);
                }
                //新增任务
                mqUtils.sendTaskMQ(loveAndFriends.getUserId(), 1, 3);
            } else {
                return returnData(StatusCode.CODE_IPS_AFFICHE_EXISTING.CODE_VALUE, "该类公告已存在", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_IPS_AFFICHE_EXISTING.CODE_VALUE, "该类公告已存在", new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + loveAndFriends.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除
     * @param id 将要删除的Id
     * @return
     */
    @Override
    public ReturnData delLove(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数ID有误", new JSONObject());
        }
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数userId有误", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的婚恋交友信息", new JSONObject());
        }
        //查询数据库
        LoveAndFriends andFriends = loveAndFriendsService.findUserById(id);
        if (andFriends == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        andFriends.setDeleteType(2);
        loveAndFriendsService.updateDel(andFriends);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新
     * @param loveAndFriends
     * @return
     */
    @Override
    public ReturnData updateLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != loveAndFriends.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + loveAndFriends.getUserId() + "]的婚恋交友信息", new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + loveAndFriends.getUserId(), 0);

        //符合推荐规则 添加到缓存home列表中
        int num3 = 0;//图片
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(loveAndFriends.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(loveAndFriends.getContent());//内容
        if (!CommonUtils.checkFull(loveAndFriends.getImgUrl())) {
            String[] imgArray = loveAndFriends.getImgUrl().split(",");
            if (imgArray != null) {
                num3 = imgArray.length;

                if (num3 == 1) {
                    fraction += 15;
                }
                if (num3 >= 6) {
                    fraction += 40;
                }
                if (num3 > 3 && num3 < 6) {
                    fraction += 30;
                }
            }
        }
        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }

        if (fraction >= 70) {
            IPS_Home ipsHome = new IPS_Home();
            ipsHome.setInfoId(loveAndFriends.getId());
            ipsHome.setTitle(loveAndFriends.getTitle());
            ipsHome.setUserId(loveAndFriends.getUserId());
            ipsHome.setContent(loveAndFriends.getContent());
            ipsHome.setMediumImgUrl(loveAndFriends.getImgUrl());
            ipsHome.setRefreshTime(loveAndFriends.getRefreshTime());
            ipsHome.setReleaseTime(loveAndFriends.getReleaseTime());
            ipsHome.setFraction(fraction);
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(1);

            //放入缓存
            redisUtils.addList(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, Constants.USER_TIME_OUT);
        }
        loveAndFriends.setFraction(fraction);
        loveAndFriends.setRefreshTime(new Date());
        loveAndFriendsService.update(loveAndFriends);

        if (!CommonUtils.checkFull(loveAndFriends.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(loveAndFriends.getUserId(), loveAndFriends.getDelImgUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 查询
     * @param id
     * @return
     */
    @Override
    public ReturnData getLove(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数id有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        LoveAndFriends loveAndFriends = null;
        Map<String, Object> loveAndFriendsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + id);
        if (loveAndFriendsMap == null || loveAndFriendsMap.size() <= 0) {
            loveAndFriends = loveAndFriendsService.findUserById(id);
            if (loveAndFriends == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //新增浏览记录
            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, loveAndFriends.getTitle(), 1);
        } else {
            //新增浏览记录
            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, loveAndFriendsMap.get("title").toString(), 1);
        }
        // 计算匹配度
        int matching = 0;// 匹配度总值
        int sex = 0;// 性别
        int age = 0;// 开始年龄
        int province = -1;// 省
        int city = -1;// 市
        int district = -1;// 区
        int studyrank = 0;// 学历
        int maritalstatus = 0;// 婚否
        int height = 0;// 身高
        int monthlyPay = 0; // 月薪
        DetailedUserInfo ua = null;
        ua = detailedUserInfoService.findUserDetailedById(CommonUtils.getMyId());
        if (ua != null) {
            height = ua.getHeight();
        }
        UserInfo userInfoCache = userInfoService.findUserById(CommonUtils.getMyId());
        if (userInfoCache != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd");
            String userbirthday = formatter.format(userInfoCache
                    .getBirthday());
            age = CommonUtils.getAge(userbirthday);
            sex = userInfoCache.getSex();
            province = userInfoCache.getProvince();
            city = userInfoCache.getCity();
            district = userInfoCache.getDistrict();
            studyrank = userInfoCache.getStudyRank();
            maritalstatus = userInfoCache.getMaritalStatus();
//            monthlyPay = userInfoCache.getMonthlyPay();
        } else {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "服务端计算婚恋交友匹配度错误，用户 [" + CommonUtils.getMyId() + "]缓存中数据异常！", new JSONObject());
        }
        // 开始计算 左上角匹配度 suntj 20161019
        loveAndFriends = loveAndFriendsService.findUserById(id);
        if (loveAndFriends.getSex() != sex) {// 匹配性别 （30%）
            matching += 30;
        }
        if (loveAndFriends.getAge() == 1) {// 匹配年龄 （10%）
            if (age >= 18 && age <= 29) {// 18-29
                matching += 10;
            }
        } else if (loveAndFriends.getAge() == 2) { // 30-39
            if (age >= 30 && age <= 39) {
                matching += 10;
            }
        } else if (loveAndFriends.getAge() == 3) {// 40-49
            if (age >= 40 && age <= 49) {
                matching += 10;
            }
        } else if (loveAndFriends.getAge() == 4) {// 50-59
            if (age >= 50 && age <= 59) {
                matching += 10;
            }
        } else if (loveAndFriends.getAge() == 5) {// 60-69
            if (age >= 60 && age <= 69) {
                matching += 10;
            }
        } else {
            if (age >= 70) {// 70岁以上
                matching += 10;
            }
        }
        if (loveAndFriends.getLocationProvince() == province) {// 匹配省市区 （省4%
            // 市4% 县2%
            // 总共10%）
            matching += 4;
            if (loveAndFriends.getLocationCity() == city) {
                matching += 4;
                if (loveAndFriends.getLocationDistrict() == district) {
                    matching += 2;
                }
            }
        }
        if (loveAndFriends.getEducation() == studyrank) {// 匹配学历 （10%）
            matching += 10;
        }
        if (loveAndFriends.getMarriage() == maritalstatus) {// 匹配婚姻状况 （20%）
            matching += 10;
        }
        if (loveAndFriends.getIncome() == monthlyPay) {// 匹配收入 （10%）
            matching += 10;
        }
        if (loveAndFriends.getStature() == height) {// 匹配身高 （10%）
            matching += 10;
        }
        loveAndFriends.setMatching(matching + "%");// 设置匹配度

        //放入缓存
        loveAndFriendsMap = CommonUtils.objectToMap(loveAndFriends);
        redisUtils.hmset(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + loveAndFriends.getUserId(), loveAndFriendsMap, Constants.USER_TIME_OUT);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", loveAndFriendsMap);
    }

    /***
     * 条件查询接口
     * @param userId   用户ID
     * @param screen  暂定按性别查询:0不限，1男，2女
     * @param sort   0智能排序，1时间倒序
     * @param page   页码 第几页 起始值1
     * @param count  每页条数
     * @return
     */
    @Override
    public ReturnData findListLove(@PathVariable long userId, @PathVariable int screen, @PathVariable int sort, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (screen < 0 || screen > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "screen参数有误", new JSONObject());
        }
        if (sort < 0 || sort > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "sort参数有误", new JSONObject());
        }
        //开始查询
        int sex = 0;
        int age = 0;
        int income = 0;
        LoveAndFriends loveAndFriends = loveAndFriendsService.findByIdUser(CommonUtils.getMyId());
        if (loveAndFriends != null) {
            sex = loveAndFriends.getSex();
            age = loveAndFriends.getAge();
            income = loveAndFriends.getIncome();
        }
        PageBean<LoveAndFriends> pageBean;
        pageBean = loveAndFriendsService.findList(userId, screen, sort, sex, age,income,page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /**
     * 查询是否已发布过
     * @param userId
     * @return
     */
    @Override
    public ReturnData publishedLove(@PathVariable long userId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数id有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> loveAndFriendsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + userId);
        if (loveAndFriendsMap == null || loveAndFriendsMap.size() <= 0) {
            LoveAndFriends loveAndFriends = loveAndFriendsService.findByIdUser(userId);
            if (loveAndFriends == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            } else {
                //放入缓存
                loveAndFriendsMap = CommonUtils.objectToMap(loveAndFriends);
                redisUtils.hmset(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + loveAndFriends.getUserId(), loveAndFriendsMap, Constants.USER_TIME_OUT);
            }
        }
        Map<String, String> idMap = new HashMap<>();
        idMap.put("infoId", loveAndFriendsMap.get("id").toString());
        return returnData(StatusCode.CODE_IPS_AFFICHE_EXISTING.CODE_VALUE, "您已发布过婚恋交友的公告，您需要修改之前的公告信息吗？", idMap);
    }

}
