package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.HotelOrdersLControllerFegin;
import com.busi.fegin.TravelOrdersLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 旅游支付业务
 * author：SunTianJie
 * create time：2020-7-31 11:39:25
 */
@Service
public class HotelOrdersService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HotelOrdersLControllerFegin hotelOrdersLControllerFegin;

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
        Map<String,Object> hotelOrderMap = redisUtils.hmget(Constants.REDIS_KEY_HOTELORDERS+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(hotelOrderMap==null||hotelOrderMap.size()<=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付酒店民宿订单失败，请重新下单",new JSONObject());
        }
        HotelOrder hotelOrder = (HotelOrder)CommonUtils.mapToObject(hotelOrderMap,HotelOrder.class);
        if(hotelOrder==null||hotelOrder.getPaymentStatus()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付酒店民宿订单失败，请重新下单!",new JSONObject());
        }
        //判断余额
        double money = hotelOrder.getMoney();//将要花费的钱
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付订单操作",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_HOTELORDERS+pay.getUserId()+"_"+pay.getOrderNumber(),"paymentStatus",1);
        hotelOrder.setPaymentStatus(1);
        hotelOrder.setPaymentTime(new Date());
        redisUtils.expire(Constants.REDIS_KEY_HOTELORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), 0);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), CommonUtils.objectToMap(hotelOrder), Constants.TIME_OUT_MINUTE_15*2);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),37,0,money*-1);//人民币转出
        //回调业务
        hotelOrdersLControllerFegin.updatePayState(hotelOrder);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
