package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.HotelTourismBookedOrderLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;

/**
 * 旅游、酒店民宿中的订座支付业务
 * author：SunTianJie
 * create time：2019/3/22 16:17
 */
@Service
public class HotelTourismBookedOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HotelTourismBookedOrderLControllerFegin hotelTourismBookedOrderLControllerFegin;

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
        Map<String,Object> hotelTourismBookedOrderMap = redisUtils.hmget(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(hotelTourismBookedOrderMap==null||hotelTourismBookedOrderMap.size()<=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付订座订单失败，请重新下单",new JSONObject());
        }
        HotelTourismBookedOrders hotelTourismBookedOrders = (HotelTourismBookedOrders)CommonUtils.mapToObject(hotelTourismBookedOrderMap,HotelTourismBookedOrders.class);
        if(hotelTourismBookedOrders==null||hotelTourismBookedOrders.getPaymentStatus()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致支付订座订单失败，请重新下单!",new JSONObject());
        }
        //判断余额
        double money = 0;//将要花费的钱
        if(hotelTourismBookedOrders.getAddToFoodMoney()>0){//判断是否为加菜
            money = hotelTourismBookedOrders.getAddToFoodMoney();//加菜
        }else{
            money = hotelTourismBookedOrders.getMoney();
        }
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行支付订单操作",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS+pay.getUserId()+"_"+pay.getOrderNumber(),"paymentStatus",1);
        hotelTourismBookedOrders.setPaymentStatus(1);
        hotelTourismBookedOrders.setPaymentTime(new Date());
        hotelTourismBookedOrders.setAddToFoodMoney(0);//将会根据这个字段来来判断是否为加菜支付
        redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), 0);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + pay.getUserId()+"_"+pay.getOrderNumber(), CommonUtils.objectToMap(hotelTourismBookedOrders), Constants.TIME_OUT_MINUTE_15*2);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),25,0,money*-1);//人民币转出 ,25订座点菜支出,26订座点菜转入
        //回调业务
        hotelTourismBookedOrderLControllerFegin.updatePayState(hotelTourismBookedOrders);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
