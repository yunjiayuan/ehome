package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.HourlyWorkerOrdersLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 小时工支付业务
 * author：SunTianJie
 * create time：2019/3/22 16:17
 */
@Service
public class HourlyWorkerOrdersService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HourlyWorkerOrdersLControllerFegin hourlyWorkerOrdersLControllerFegin;

    @Autowired
    private MqUtils mqUtils;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {
        //获取未支付的订单
        Map<String,Object> hourlyWorkerOrdersMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYORDERS+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(hourlyWorkerOrdersMap==null||hourlyWorkerOrdersMap.size()<=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付小时工订单失败，请重新下单",new JSONObject());
        }
        HourlyWorkerOrders hourlyWorkerOrders = (HourlyWorkerOrders)CommonUtils.mapToObject(hourlyWorkerOrdersMap,HourlyWorkerOrders.class);
        if(hourlyWorkerOrders==null||hourlyWorkerOrders.getOrdersType()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付小时工订单失败，请重新下单!",new JSONObject());
        }
        //判断余额
        double money = hourlyWorkerOrders.getMoney();//将要花费的钱
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付订单操作",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_HOURLYORDERS+pay.getUserId()+"_"+pay.getOrderNumber(),"ordersType",8);
        hourlyWorkerOrders.setOrdersType(8);//已支付
        hourlyWorkerOrders.setPaymentTime(new Date());
        redisUtils.expire(Constants.REDIS_KEY_HOURLYORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), 0);
        redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), CommonUtils.objectToMap(hourlyWorkerOrders), Constants.USER_TIME_OUT);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),23,0,money*-1);//人民币转出
        //回调业务
        hourlyWorkerOrdersLControllerFegin.updatePayStates(hourlyWorkerOrders);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
