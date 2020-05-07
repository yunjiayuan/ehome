package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.ShopFloorOrdersLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 支付--楼店订单业务
 * author：SunTianJie
 * create time：2019-12-19 15:16:16
 */
@Service
public class ShopFloorOrdersService extends BaseController implements PayBaseService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private ShopFloorOrdersLControllerFegin shopFloorOrdersLControllerFegin;

    /**
     * 具体支付业务
     *
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {

        Map<String, Object> shopFloorOrderMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOORORDERS + pay.getUserId() + "_" + pay.getOrderNumber());
        if (shopFloorOrderMap == null || shopFloorOrderMap.size() <= 0) {
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE, "由于您等待时间过久或网络延迟导致支付楼店订单失败，请重新支付", new JSONObject());
        }
        ShopFloorOrders shopFloorOrders = (ShopFloorOrders) CommonUtils.mapToObject(shopFloorOrderMap, ShopFloorOrders.class);
        if (shopFloorOrders == null || shopFloorOrders.getOrdersType() != 0) {
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE, "由于您等待时间过久或网络延迟导致支付楼店订单失败，请重新支付!", new JSONObject());
        }
        //判断余额
        double money = shopFloorOrders.getMoney();//将要支付的钱
        double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if (serverMoney < money) {
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE, "您账户余额不足，无法支付楼店订单", new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_SHOPFLOORORDERS + pay.getUserId() + "_" + pay.getOrderNumber(), "ordersType", 1);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(), 28, 0, money * -1);//人民币转出
        //回调业务
        ShopFloorOrders shopfOrders = new ShopFloorOrders();
        shopfOrders.setId(shopFloorOrders.getId());
        if (shopFloorOrders.getType() == 1 || shopFloorOrders.getType() == 2) {//礼尚往来订单
            if (shopFloorOrders.getRecipientId() > 0) {
                shopfOrders.setOrdersType(1);//待发货（已付款）
            } else {
                shopfOrders.setOrdersType(8);//待送出（已付款未指定接受者，礼尚往来订单有效）
            }
        } else {//普通
            shopfOrders.setOrdersType(1);//待发货（已付款）
        }
        shopfOrders.setPaymentTime(new Date());
        shopFloorOrdersLControllerFegin.updatePayType(shopfOrders);
        //清除缓存中的订单记录
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + pay.getUserId() + "_" + pay.getOrderNumber(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
