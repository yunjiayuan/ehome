package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.ShopFloorBondLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付--楼店缴费业务
 * author：SunTianJie
 * create time：2018/8/28 15:21
 */
@Service
public class ShopFloorBondOrdersService extends BaseController implements PayBaseService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private ShopFloorBondLControllerFegin shopFloorBondLControllerFegin;

    /**
     * 具体支付业务
     *
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {

        Map<String, Object> shopFloorBondOrderMap = redisUtils.hmget(Constants.REDIS_KEY_BONDORDER + pay.getUserId() + "_" + pay.getOrderNumber());
        if (shopFloorBondOrderMap == null || shopFloorBondOrderMap.size() <= 0) {
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE, "由于您等待时间过久或网络延迟导致支付楼店保证金失败，请重新支付", new JSONObject());
        }
        ShopFloorBondOrders shopFloorBondOrders = (ShopFloorBondOrders) CommonUtils.mapToObject(shopFloorBondOrderMap, ShopFloorBondOrders.class);
        if (shopFloorBondOrders == null || shopFloorBondOrders.getPayState() != 0) {
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE, "由于您等待时间过久或网络延迟导致支付楼店保证金失败，请重新支付!", new JSONObject());
        }
        //判断余额
        double money = shopFloorBondOrders.getMoney();//将要兑换的钱
        double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if (serverMoney < money) {
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE, "您账户余额不足，无法进行缴纳楼店保证金操作", new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_BONDORDER + pay.getUserId() + "_" + pay.getOrderNumber(), "payState", 1);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(), 29, 0, money * -1);//人民币转出
        //回调业务
        ShopFloor shopFloor = new ShopFloor();
        shopFloor.setPayState(1);//已支付
        shopFloor.setUserId(shopFloorBondOrders.getUserId());
        shopFloor.setVillageOnly(shopFloorBondOrders.getVillageOnly());//楼店唯一标识
        shopFloorBondLControllerFegin.updatePayStates(shopFloor);
        //清除缓存中的订单记录
        redisUtils.expire(Constants.REDIS_KEY_BONDORDER + pay.getUserId() + "_" + pay.getOrderNumber(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
