package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Pay;
import com.busi.entity.Purse;
import com.busi.entity.RentAhouseOrder;
import com.busi.entity.ReturnData;
import com.busi.fegin.RentAhouseOrderLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 处理需求汇：租房购房订单状态修改业务
 * author：SunTianJie
 * create time：2018/9/7 14:22
 */
@Service
public class RentAhouseOrdersLService extends BaseController implements PayBaseService{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RentAhouseOrderLControllerFegin rentAhouseOrderLControllerFegin;

    /***
     * 修改订单支付状态为 已支付
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {
        //获取未支付的订单
        Map<String,Object> rentAhouseOrdersMap = redisUtils.hmget(Constants.REDIS_KEY_RENTAHOUSE_ORDER+pay.getOrderNumber() );
        if(rentAhouseOrdersMap==null){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该订单已过期，请重新下单!",new JSONObject());
        }
        RentAhouseOrder rentAhouseOrder = (RentAhouseOrder) CommonUtils.mapToObject(rentAhouseOrdersMap,RentAhouseOrder.class);
        if(rentAhouseOrder==null||rentAhouseOrder.getPaymentStatus()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该订单已过期，请重新下单!",new JSONObject());
        }
        //判断余额
        double money = rentAhouseOrder.getMoney();//将要支付的钱
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买操作",new JSONObject());
        }
        //更改状态 防止重复支付
        rentAhouseOrder.setPaymentStatus(1);
        rentAhouseOrder.setPaymentTime(new Date());
        redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE_ORDER + pay.getOrderNumber(), 0);
        redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + pay.getOrderNumber(), CommonUtils.objectToMap(rentAhouseOrder), Constants.TIME_OUT_MINUTE_60_24_1*7);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),48,0,money*-1);//人民币转出
        //回调业务
        rentAhouseOrderLControllerFegin.updatePayType(rentAhouseOrder);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
