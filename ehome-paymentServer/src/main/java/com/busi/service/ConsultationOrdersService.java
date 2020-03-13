package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.ShopFloorBondLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付律师医生咨询费用业务
 * author：SunTianJie
 * create time：2018/8/28 15:21
 */
@Service
public class ConsultationOrdersService extends BaseController implements PayBaseService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    /**
     * 具体支付业务
     *
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {

        Map<String, Object> consultationOrdersMap = redisUtils.hmget(Constants.REDIS_KEY_CONSULTATIONORDER + pay.getUserId() + "_" + pay.getOrderNumber());
        if (consultationOrdersMap == null || consultationOrdersMap.size() <= 0) {
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE, "由于您等待时间过久或网络延迟导致支付资费失败，请重新支付", new JSONObject());
        }
        ConsultationOrders consultationOrders = (ConsultationOrders) CommonUtils.mapToObject(consultationOrdersMap, ConsultationOrders.class);
        if (consultationOrders == null || consultationOrders.getPayState() != 0) {
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE, "由于您等待时间过久或网络延迟导致支付资费失败，请重新支付!", new JSONObject());
        }
        //判断余额
        double money = consultationOrders.getMoney();//将要支付的钱
        double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if (serverMoney < money) {
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE, "您账户余额不足，无法进行支付资费操作", new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_BONDORDER + pay.getUserId() + "_" + pay.getOrderNumber(), "payState", 1);
        //开始扣款支付
        if(consultationOrders.getOccupation()==0){//医生
            mqUtils.sendPurseMQ(pay.getUserId(), 30, 0, money * -1);//人民币转出
        }else{//律师
            mqUtils.sendPurseMQ(pay.getUserId(), 31, 0, money * -1);//人民币转出
        }
        //清除缓存中的订单记录
        redisUtils.expire(Constants.REDIS_KEY_CONSULTATIONORDER + pay.getUserId() + "_" + pay.getOrderNumber(), 0);
        Map<String,Integer> map = new HashMap();
        map.put("duration",consultationOrders.getDuration());//返回咨询时长
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
