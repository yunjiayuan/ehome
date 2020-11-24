package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.DoorwayBusinessOrder;
import com.busi.entity.Pay;
import com.busi.entity.Purse;
import com.busi.entity.ReturnData;
import com.busi.fegin.DoorwayBusinessOrdersLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 隐形商家支付业务
 * author：SunTianJie
 * create time：2020-11-23 14:28:50
 */
@Service
public class DoorwayBusinessOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DoorwayBusinessOrdersLControllerFegin doorwayBusinessOrdersLControllerFegin;

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
        Map<String,Object> doorwayBusinessOrderMap = redisUtils.hmget(Constants.REDIS_KEY_DOORWAYBUSINESSORDERS+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(doorwayBusinessOrderMap==null||doorwayBusinessOrderMap.size()<=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付家门口商家订单失败，请重新下单",new JSONObject());
        }
        DoorwayBusinessOrder doorwayBusinessOrder = (DoorwayBusinessOrder)CommonUtils.mapToObject(doorwayBusinessOrderMap,DoorwayBusinessOrder.class);
        if(doorwayBusinessOrder==null||doorwayBusinessOrder.getPaymentStatus()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付家门口商家订单失败，请重新下单!",new JSONObject());
        }
        //判断余额
        double money = doorwayBusinessOrder.getMoney();//将要花费的钱
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付订单操作",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_PHARMACYORDERS+pay.getUserId()+"_"+pay.getOrderNumber(),"paymentStatus",1);
        doorwayBusinessOrder.setPaymentStatus(1);
        doorwayBusinessOrder.setPaymentTime(new Date());
        redisUtils.expire(Constants.REDIS_KEY_PHARMACYORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), 0);
        redisUtils.hmset(Constants.REDIS_KEY_PHARMACYORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), CommonUtils.objectToMap(doorwayBusinessOrder), Constants.TIME_OUT_MINUTE_15*2);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),44,0,money*-1);//人民币转出
        //回调业务
        doorwayBusinessOrdersLControllerFegin.updatePayState(doorwayBusinessOrder);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
