package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Pay;
import com.busi.entity.RedPacketsCensus;
import com.busi.entity.RedPacketsInfo;
import com.busi.entity.ReturnData;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 处理红包订单状态修改业务
 * author：SunTianJie
 * create time：2018/9/7 14:22
 */
@Service
public class RedPacketsInfoOrderService  extends BaseController implements PayBaseService{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedPacketsInfoService redPacketsInfoService;

    @Autowired
    RedPacketsCensusService redPacketsCensusService;

    /***
     * 修改红包支付状态和收取状态
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {
        //获取未支付的订单
        Map<String,Object> redPacketsInfoMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_REDPACKETSINFO+pay.getOrderNumber() );
        RedPacketsInfo redPacketsInfo = (RedPacketsInfo)CommonUtils.mapToObject(redPacketsInfoMap,RedPacketsInfo.class);
        if(redPacketsInfo==null){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该红包已过期，无法再进行操作",new JSONObject());
        }
        RedPacketsCensus rpc = null;
        switch (pay.getServiceType()) {
            case 3://发红包
                //判断余额
                double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
                if(serverMoney<redPacketsInfo.getRedPacketsMoney()){
                    return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法发送红包",new JSONObject());
                }
                if(redPacketsInfo.getPayStatus()!=0){//已支付
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"该红包已支付，无法再进行支付",new JSONObject());
                }
                //当前用户是否有权限
                if(pay.getUserId()!=redPacketsInfo.getSendUserId()){
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"您无权支付当前红包",new JSONObject());
                }
                //更改状态 防止重复支付
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_REDPACKETSINFO+pay.getOrderNumber() ,"payStatus",1);
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),5,0,redPacketsInfo.getRedPacketsMoney()*-1);
                //回调业务 更新会员状态
                redPacketsInfo.setPayStatus(1);//已支付
                redPacketsInfoService.updateRedPacketsPayStatus(redPacketsInfo);
                //更新红包订单生命周期 时间为 24小时(忽略支付时浪费的时间误差)
                redisUtils.expire(Constants.REDIS_KEY_PAY_ORDER_REDPACKETSINFO+pay.getOrderNumber(),Constants.TIME_OUT_MINUTE_60_24_1);//设置过期0秒
                //更新统计
                rpc = redPacketsCensusService.findRedPacketsCensus(redPacketsInfo.getSendUserId());
                if(rpc==null){
                    rpc = new RedPacketsCensus();
                    rpc.setUserId(redPacketsInfo.getSendUserId());
                    rpc.setSendAmount(redPacketsInfo.getRedPacketsMoney());//发红包总金额
                    rpc.setSendCounts(1);//发红包总数
                    redPacketsCensusService.addRedPacketsCensus(rpc);
                }else{
                    rpc.setSendAmount(rpc.getSendAmount()+redPacketsInfo.getRedPacketsMoney());//发红包总金额
                    rpc.setSendCounts(rpc.getSendCounts()+1);//发红包总数
                    redPacketsCensusService.updateredPacketsCensus(rpc);
                }
                break;
            case 4://拆红包
                if(redPacketsInfo.getRedPacketsStatus()!=0){//未收取
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"该红包对方尚未支付，无法进行拆红包",new JSONObject());
                }
                //当前用户是否有权限
                if(pay.getUserId()!=redPacketsInfo.getReceiveUserId()){
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"您无权收取当前红包",new JSONObject());
                }
                //更改状态 防止重复支付
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_REDPACKETSINFO+pay.getOrderNumber() ,"redPacketsStatus",2);
                //开始将红包放入账户
                mqUtils.sendPurseMQ(pay.getUserId(),4,0,redPacketsInfo.getRedPacketsMoney());
                //回调业务 更新会员状态
                redPacketsInfo.setRedPacketsStatus(2);//已拆（已接收）
                redPacketsInfo.setReceiveTime(new Date());
                redPacketsInfoService.updateRedPacketsStatus(redPacketsInfo);
                //更新统计
                rpc = redPacketsCensusService.findRedPacketsCensus(redPacketsInfo.getReceiveUserId());
                if(rpc==null){
                    rpc = new RedPacketsCensus();
                    rpc.setUserId(redPacketsInfo.getReceiveUserId());
                    rpc.setReceivedAmount(redPacketsInfo.getRedPacketsMoney());//发红包总金额
                    rpc.setReceivedCounts(1);//发红包总数
                    redPacketsCensusService.addRedPacketsCensus(rpc);
                }else{
                    rpc.setReceivedAmount(rpc.getReceivedAmount()+redPacketsInfo.getRedPacketsMoney());//发红包总金额
                    rpc.setReceivedCounts(rpc.getReceivedCounts()+1);//发红包总数
                    redPacketsCensusService.updateredPacketsCensus(rpc);
                }
                break;
            default:
                break;
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
