package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.TaskService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @program: 红包雨相关接口
 * @author: ZHaoJiaJie
 * @create: 2018-09-12 11:15
 */
@RestController
public class RedBagRainController extends BaseController implements RedBagRainApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TaskService taskService;

    @Autowired
    UserInfoUtils userInfoUtils;

    /***
     * 查询任务完成度
     * @return
     */
    @Override
    public ReturnData findTaskList() {
        int num = 0;
        Map<String, Integer> isMap = new HashMap<>();
        num = taskService.findNum(CommonUtils.getMyId());
        if (num >= 3) {
            isMap.put("state", 1);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "用户：[" + CommonUtils.getMyId() + " ]当天任务完成度已满足红包雨条件！", isMap);
        }
        isMap.put("state", 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "用户：[" + CommonUtils.getMyId() + " ]当天任务完成度未满足红包雨条件", isMap);
    }

    /***
     * 拆红包
     * @param paymentKey  私钥
     * @return
     */
    @Override
    public ReturnData dismantling(@PathVariable String paymentKey) {
        //验证参数
        if (CommonUtils.checkFull(paymentKey)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数paymentKey有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
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
        int num = 0;
        int awardsId = 1;//奖品,0谢谢参与 1现金
        double spareMoney = 0.00;//现金具体数值

        Random r = new Random();
        int romAwardsId = r.nextInt(15) + 1;
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        if (romAwardsId <= 9) {//返现金0-2元
            romAwardsId = r.nextInt(10) + 1;
            if (romAwardsId > 5) {
                num = r.nextInt(1000) + 1;
            } else {
                awardsId = 0;
            }
        } else if (romAwardsId > 9 && romAwardsId <= 12) {//返现金2-5元
            num = r.nextInt(1501) + 1000;
        } else if (romAwardsId > 12 && romAwardsId <= 14) {//返现金5-8元
            romAwardsId = r.nextInt(10) + 1;
            if (romAwardsId > 7) {
                num = r.nextInt(1501) + 2500;
            } else {
                awardsId = 0;
            }
        } else if (romAwardsId > 14) {//返现金8-10元
            romAwardsId = r.nextInt(10) + 1;
            if (romAwardsId % 2 == 0) {
                num = r.nextInt(1001) + 4000;
            } else {
                awardsId = 0;
            }
        }
        double num2 = (num / 500.00);
        String num3 = dcmFmt.format(num2);
        spareMoney = Double.parseDouble(num3);

        //更新钱包余额和钱包明细
//        mqUtils.sendPurseMQ(myId, 4, 0, spareMoney);
        //新增用户奖励记录
        if (spareMoney > 0) {
            mqUtils.addRewardLog(myId, 0, 0, spareMoney, 0);
        }
        //添加记录
        RedBagRain rain = new RedBagRain();
        rain.setUserId(myId);
        rain.setPizeType(awardsId);
        rain.setQuota(spareMoney);
        rain.setTime(new Date());
        taskService.addRain(rain);
        Map<String, Object> isMap = new HashMap<>();
        isMap.put("awardsId", awardsId);
        isMap.put("spareMoney", spareMoney);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", isMap);
    }

    /***
     * 分页查询红包雨中奖记录
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findPrizeList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数userId有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<RedBagRain> pageBean;
        pageBean = taskService.findPrizeList(userId, page, count);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * 分页查询红包雨中奖名单列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findRedBagList(@PathVariable int page, @PathVariable int count) {

        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        RedBagRain t = null;
        PageBean<RedBagRain> pageBean;
        pageBean = taskService.findRedBagList(page, count);
        List<RedBagRain> theList = new ArrayList<RedBagRain>();
        if (pageBean != null) {
            list = pageBean.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < 10; i++) {
                    UserInfo userInfo = null;
                    t = (RedBagRain) list.get(i);
                    if (t != null) {
                        userInfo = userInfoUtils.getUserInfo(t.getUserId());
                        if (userInfo != null) {
                            t.setName(userInfo.getName());
                            t.setHead(userInfo.getHead());
                            t.setProTypeId(userInfo.getProType());
                            t.setHouseNumber(userInfo.getHouseNumber());
                        }
                        theList.add(t);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, theList);
    }
}
