package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;

/**
 * 处理转账订单状态修改业务
 * author：SunTianJie
 * create time：2020-7-2 08:34:39
 */
@Service
public class TransferAccountsInfoOrderService extends BaseController implements PayBaseService{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    TransferAccountsInfoService transferAccountsInfoService;

    /***
     * 修改转账支付状态、收取状态、退还状态
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {
        //获取缓存中的订单
        Map<String,Object> transferAccountsInfoMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_TRANSFERACCOUNTSINFO+pay.getOrderNumber() );
        TransferAccountsInfo transferAccountsInfo = null;
        switch (pay.getServiceType()) {
            case 20://发送转账
                if(transferAccountsInfoMap==null||transferAccountsInfoMap.size()<=0){
                    return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该转账操作已过期，无法再进行操作",new JSONObject());
                }
                transferAccountsInfo = (TransferAccountsInfo)CommonUtils.mapToObject(transferAccountsInfoMap,TransferAccountsInfo.class);
                if(transferAccountsInfo==null){
                    return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该转账操作已过期，请重新转账",new JSONObject());
                }
                //判断余额
                double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
                if(serverMoney<transferAccountsInfo.getTransferAccountsMoney()){
                    return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行转账",new JSONObject());
                }
                if(transferAccountsInfo.getPayStatus()!=0){//已支付
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"该转账已成功，无法再进行重复转账",new JSONObject());
                }
                //当前用户是否有权限
                if(pay.getUserId()!=transferAccountsInfo.getSendUserId()){
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"您无权使用其他人的账号进行转账操作",new JSONObject());
                }
                //更改状态 防止重复支付
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_TRANSFERACCOUNTSINFO+pay.getOrderNumber() ,"payStatus",1);
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),34,0,transferAccountsInfo.getTransferAccountsMoney()*-1);
                //回调业务 更新会员状态
                transferAccountsInfo.setPayStatus(1);//已支付
                transferAccountsInfoService.updateTransferAccountsInfoPayStatus(transferAccountsInfo);
                //更新转账订单生命周期 时间为 24小时(忽略支付时浪费的时间误差)
                redisUtils.expire(Constants.REDIS_KEY_PAY_ORDER_TRANSFERACCOUNTSINFO+pay.getOrderNumber(),Constants.TIME_OUT_MINUTE_60_24_1);//设置过期0秒
                break;
            case 21://1接收转账
                //缓存中的订单不存在  则可以已过期或者缓存出现异常 补偿处理
                if(transferAccountsInfoMap==null||transferAccountsInfoMap.size()<=0){
                    //查询数据库
                    transferAccountsInfo = transferAccountsInfoService.findTransferAccountsInfo(pay.getUserId(),pay.getOrderNumber());
                    if(transferAccountsInfo==null){
                        return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该转账已过期，无法操作",new JSONObject());
                    }
                }else{
                    transferAccountsInfo = (TransferAccountsInfo)CommonUtils.mapToObject(transferAccountsInfoMap,TransferAccountsInfo.class);
                    if(transferAccountsInfo==null){
                        transferAccountsInfo = transferAccountsInfoService.findTransferAccountsInfo(pay.getUserId(),pay.getOrderNumber());
                        if(transferAccountsInfo==null){
                            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该转账已过期，无法操作",new JSONObject());
                        }
                    }
                }
                if(transferAccountsInfo.getTransferAccountsStatus()!=0){//非未收取状态无法进行接收操作
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"该转账对方尚未支付或已过期，无法进行接收操作",new JSONObject());
                }
                //当前用户是否有权限
                if(pay.getUserId()!=transferAccountsInfo.getReceiveUserId()){
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"您无权接收当前转账",new JSONObject());
                }
                //更改状态 防止重复接收
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_TRANSFERACCOUNTSINFO+pay.getOrderNumber() ,"transferAccountsStatus",2);
                //开始将转账金额放入账户
                mqUtils.sendPurseMQ(pay.getUserId(),35,0,transferAccountsInfo.getTransferAccountsMoney());
                //回调业务
                transferAccountsInfo.setTransferAccountsStatus(2);//已拆（已接收）
                transferAccountsInfo.setReceiveTime(new Date());
                transferAccountsInfoService.updateTransferAccountsInfo(transferAccountsInfo);
                break;
            default:
                break;
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
