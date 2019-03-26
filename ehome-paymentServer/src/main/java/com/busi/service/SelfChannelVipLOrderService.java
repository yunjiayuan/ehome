package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.SelfChannelVipLControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 支付--购买自频道会员业务（云视频）
 * author：SunTianJie
 * create time：2018/8/28 15:21
 */
@Service
public class SelfChannelVipLOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private SelfChannelVipLControllerFegin selfChannelVipLControllerFegin;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay,Map<String,Object> purseMap) {

        //获取未支付的订单
        Map<String,Object> selfChannelVipOrderMap = redisUtils.hmget(Constants.REDIS_KEY_SELFCHANNELVIP_ORDER+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(selfChannelVipOrderMap==null||selfChannelVipOrderMap.size()<=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致购买自频道会员失败，请重新购买",new JSONObject());
        }
        SelfChannelVipOrder selfChannelVipOrder = (SelfChannelVipOrder) CommonUtils.mapToObject(selfChannelVipOrderMap,SelfChannelVipOrder.class);
        if(selfChannelVipOrder==null||selfChannelVipOrder.getPayState()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致购买自频道会员失败，请重新购买!",new JSONObject());
        }
        //判断余额
        double money = selfChannelVipOrder.getMoney();//将要花费的钱
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买自频道会员操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买自频道会员操作!",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_SELFCHANNELVIP_ORDER+pay.getUserId()+"_"+pay.getOrderNumber(),"payState",1);
        redisUtils.expire(Constants.REDIS_KEY_SELFCHANNELVIP_ORDER + pay.getUserId()+"_"+pay.getOrderNumber(), 0);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),17,0,money*-1);//人民币转出 购买会员支出
        //回调业务
        SelfChannelVip selfChannelVip = new SelfChannelVip();
        selfChannelVip.setUserId(pay.getUserId());
//        selfChannelVip.setMemberShipStatus(0);//已开通
        selfChannelVipLControllerFegin.addSelfMember(selfChannelVip);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
