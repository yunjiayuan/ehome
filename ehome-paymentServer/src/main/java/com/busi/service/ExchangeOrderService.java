package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Pay;
import com.busi.entity.Purse;
import com.busi.entity.PursePayPassword;
import com.busi.entity.ReturnData;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付--钱包兑换业务
 * author：SunTianJie
 * create time：2018/8/28 15:21
 */
@Service
public class ExchangeOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay,Map<String,Object> purseMap) {

        //获取未支付的订单
        Map<String,Object> exchangeOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_EXCHANGE+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(exchangeOrderMap==null||exchangeOrderMap.size()<=0||Integer.parseInt(exchangeOrderMap.get("payStatus").toString())!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致兑换失败，请重新兑换",new JSONObject());
        }
        //判断余额
        long money = Long.parseLong(exchangeOrderMap.get("money").toString());//将要兑换的钱
        if(Integer.parseInt(exchangeOrderMap.get("ExchangeType").toString())==1){
            //人民币兑换家币
            double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
            if(serverMoney<money){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行兑换操作",new JSONObject());
            }
            //开始扣款支付
            mqUtils.sendPurseMQ(pay.getUserId(),10,1,money);//家币转入
            mqUtils.sendPurseMQ(pay.getUserId(),11,0,money*-1);//人民币转出
        }else{
            //家币兑换家点
            long serverHomePoint = Long.parseLong(purseMap.get("homePoint").toString());
            if(serverHomePoint<money){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行兑换操作",new JSONObject());
            }
            //开始扣款支付
            mqUtils.sendPurseMQ(pay.getUserId(),10,2,money*100);//家点转入
            mqUtils.sendPurseMQ(pay.getUserId(),11,1,money*-1);//家币转出
        }

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
