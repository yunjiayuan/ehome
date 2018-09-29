package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.SharingPromotionService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @program: ehome
 * @description: 新人分享红包信息
 * @author: ZHaoJiaJie
 * @create: 2018-09-27 15:07
 */
@RestController
public class SharingPromotionController extends BaseController implements SharingPromotionApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    SharingPromotionService sharingPromotionService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 查询红包记录
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findShareList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        long counts = 0;//接收的红包总数
        double tatolAmount = 0.0;//接收的红包总金额
        PageBean<ShareRedPacketsInfo> pageBean;
        pageBean = sharingPromotionService.findList(page, count, CommonUtils.getMyId());
        List redList = pageBean.getList();
        if (redList != null && redList.size() > 0) {
            counts = sharingPromotionService.findNum(CommonUtils.getMyId());
            tatolAmount = sharingPromotionService.findSum(CommonUtils.getMyId());
            for (int i = 0; i < redList.size(); i++) {
                ShareRedPacketsInfo t = null;
                t = (ShareRedPacketsInfo) redList.get(i);
                if (t != null) {
                    UserInfo userInfo = null;
                    userInfo = userInfoUtils.getUserInfo(t.getBeSharedUserId());
                    if (userInfo != null) {
                        t.setBeSharedUserName(userInfo.getName());
                        t.setBeSharedProTypeId(userInfo.getProType());
                        t.setBeSharedUserHead(userInfo.getHead());
                        t.setBeSharedHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
        }
        //获取自己的用户信息
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(CommonUtils.getMyId());
        Map<String, Object> map = new HashMap<>();
        map.put("data", redList);
        if (redList != null && redList.size() > 0) {
            map.put("counts", counts);
            map.put("tatolAmount", tatolAmount);
        }
        map.put("head", userInfo.getHead());
        map.put("name", userInfo.getName());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 领红包
     * @param paymentKey  私钥
     * @param shareCode 分享码
     * @return
     */
    @Override
    public ReturnData receive(@PathVariable String paymentKey, @PathVariable String shareCode) {
        //验证参数
        if (CommonUtils.checkFull(paymentKey)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "paymentKey参数有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        //判断缓存中是否有此人操作记录(是否领取新人红包)
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_SHARING_PROMOTION + myId);
        if (map != null && map.size() > 0) {
            return returnData(StatusCode.CODE_ALREADY_RECEIVED_ERROR.CODE_VALUE, "该用户已领新人红包,不能重复领取！", new JSONObject());
        }
        //验证支付秘钥是否正确
        Object serverKey = redisUtils.getKey(Constants.REDIS_KEY_PAYMENT_PAYKEY + myId);
        if (serverKey == null) {
            if (CommonUtils.checkFull(paymentKey)) {
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT, myId + "", 1, 24 * 60 * 60);//设置1天后失效
            } else {
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT, myId + "", 1);
            }
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE, "操作已过期，秘钥已过期，请稍后重试!", new JSONObject());
        }
        if (!paymentKey.equals(serverKey.toString())) {
            if (CommonUtils.checkFull(paymentKey)) {
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT, myId + "", 1, 24 * 60 * 60);//设置1天后失效
            } else {
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT, myId + "", 1);
            }
            //清除秘钥
            redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYKEY + myId, 0);
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE, "秘钥不正确，请稍后重试!", new JSONObject());
        }
        //清除秘钥
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYKEY + myId, 0);
        //判断是否是新人
        UserInfo userInfo = null;
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + myId);
        if (userMap == null || userMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserInfo u = null;
            u = userInfoUtils.getUserInfo(myId);
            if (u == null) {//数据库也没有
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "用户不存在!", new JSONObject());
            }
            userMap = CommonUtils.objectToMap(u);
            redisUtils.hmset(Constants.REDIS_KEY_USER + myId, userMap, Constants.USER_TIME_OUT);
        }
        userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
        if (userInfo.getIsNewUser() != 0) {
            return returnData(StatusCode.CODE_ALREADY_RECEIVED_ERROR.CODE_VALUE, "该用户已领新人红包,不能重复领取！", new JSONObject());
        }
        //判断该用户是否绑定手机
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(myId);
        if (userAccountSecurity != null) {
            if (CommonUtils.checkFull(userAccountSecurity.getPhone())) {
                return returnData(StatusCode.CODE_NOT_BIND_PHONE_ERROR.CODE_VALUE, "该用户未绑定手机号!", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_NOT_BIND_PHONE_ERROR.CODE_VALUE, "该用户未绑定手机号!", new JSONObject());
        }
        double redPacketsMoney = 0.00;
        //判断是否有分享码
        String proId = "";
        if (!CommonUtils.checkFull(shareCode)) {
            //判断第一位是否为0  分享码格式为 001001518 前两位为省简称ID
            if (shareCode.indexOf("0") != 0) {
                proId = shareCode.substring(0, 2);
            } else {
                proId = shareCode.substring(1, 2);
            }
            long userId = 0;
            Map<String, Object> userIdMap = redisUtils.hmget(Constants.REDIS_KEY_HOUSENUMBER);
            if (userIdMap == null || userIdMap.size() <= 0) {
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "用户不存在!", new JSONObject());
            }
            for (String key : userIdMap.keySet()) {
                Object object = key;
                if (object.toString().equals(proId + "_" + shareCode.substring(2))) {
                    userId = Long.valueOf(String.valueOf(userIdMap.get(key)));
                    break;
                }
            }
            if (userId <= 0) {
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "用户不存在!", new JSONObject());
            }
            //验证参数
            if (myId == userId) {
                return returnData(StatusCode.CODE_SHARE_CODE_ERROR2.CODE_VALUE, "分享码有误,分享码不能是自己的", new JSONObject());
            }
            //分享者分享人数 10人5元  30人20元 100人50元 200人100元
            Long shareCount = sharingPromotionService.findPeople(userId);
            if (shareCount == 10) {
                redPacketsMoney = 5;
            } else if (shareCount == 30) {
                redPacketsMoney = 20;
            } else if (shareCount == 100) {
                redPacketsMoney = 50;
            } else if (shareCount == 200) {
                redPacketsMoney = 100;
            } else {
                redPacketsMoney = 0;
            }
            if (redPacketsMoney == 0) {
                //生成随机红包
                Random rand = new Random();
                int randNum = rand.nextInt(100) + 1;
                redPacketsMoney = randNum / 100.0;
            }
            //更新分享者钱包余额和钱包明细
            mqUtils.sendPurseMQ(userId, 4, 0, redPacketsMoney);

            //新增分享者红包记录
            ShareRedPacketsInfo srp = new ShareRedPacketsInfo();
            srp.setBeSharedUserId(myId);
            srp.setShareUserId(userId);
            srp.setRedPacketsMoney(redPacketsMoney);
            srp.setTime(new Date());
            sharingPromotionService.add(srp);
        }
        //开始生成新人红包
        //生成随机红包
        Random rand = new Random();
        int randNum = rand.nextInt(100) + 1;
        redPacketsMoney = randNum / 100.0;

        //更新新人钱包余额和钱包明细
        mqUtils.sendPurseMQ(myId, 4, 0, redPacketsMoney);

        //更新用户状态
        if (userInfo != null) {
            userInfo.setIsNewUser(1);
            userInfoUtils.updateIsNew(userInfo);
        }
        //放入缓存
        ShareRedPacketsInfo myAttr = new ShareRedPacketsInfo();
        myAttr.setShareUserId(myId);
        Map<String, Object> myAttrMap = CommonUtils.objectToMap(myAttr);
        redisUtils.hmset(Constants.REDIS_KEY_SHARING_PROMOTION + myId, myAttrMap, 0);

        Map<String, Double> mapMoney = new HashMap<>();
        mapMoney.put("redPacketsMoney", redPacketsMoney);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", mapMoney);
    }
}
