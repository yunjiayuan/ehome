package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.GoodNumberOrdersLControllerFegin;
import com.busi.fegin.UserInfoLocalControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付--购买靓号业务
 * author：SunTianJie
 * create time：2019-4-1 19:04:22
 */
@Service
public class GoodNumberOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private GoodNumberOrdersLControllerFegin goodNumberOrdersLControllerFegin;

    @Autowired
    private UserInfoLocalControllerFegin userInfoLocalControllerFegin;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay,Map<String,Object> purseMap) {

        //获取未支付的订单
        Map<String,Object> goodNumberOrderMap = redisUtils.hmget(Constants.REDIS_KEY_GOODNUMBER_ORDER+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(goodNumberOrderMap==null||goodNumberOrderMap.size()<=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致购买靓号失败，请重新购买",new JSONObject());
        }
        GoodNumberOrder goodNumberOrder = (GoodNumberOrder) CommonUtils.mapToObject(goodNumberOrderMap,GoodNumberOrder.class);
        if(goodNumberOrder==null||goodNumberOrder.getPayState()!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致购买靓号失败，请重新购买!",new JSONObject());
        }
        //判断余额
        double money = goodNumberOrder.getMoney();//将要花费的钱
        Purse purse = (Purse) CommonUtils.mapToObject(purseMap,Purse.class);
        if(purse==null){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买靓号操作",new JSONObject());
        }
        double serverMoney = purse.getSpareMoney();
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买靓号操作!",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_GOODNUMBER_ORDER+pay.getUserId()+"_"+pay.getOrderNumber(),"payState",1);
        redisUtils.expire(Constants.REDIS_KEY_GOODNUMBER_ORDER + pay.getUserId()+"_"+pay.getOrderNumber(), 0);
        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),22,0,money*-1);//人民币转出 购买靓号支出
        //回调业务 更新预售靓号状态
        GoodNumber goodNumber = new GoodNumber();
        goodNumber.setProId(goodNumberOrder.getProId());
        goodNumber.setHouse_number(goodNumberOrder.getHouse_number());
        goodNumber.setStatus(1);
        goodNumberOrdersLControllerFegin.updateGoodNumber(goodNumber);
        //新增新用户
        UserInfo userInfo = new UserInfo();
        userInfo.setProType(goodNumberOrder.getProId());
        userInfo.setHouseNumber(goodNumberOrder.getHouse_number());
        userInfo.setName(goodNumberOrder.getName());
        userInfo.setPassword(goodNumberOrder.getPassword());
        userInfo.setSex(goodNumberOrder.getSex());
        userInfo.setBirthday(goodNumberOrder.getBirthday());
        userInfo.setCountry(goodNumberOrder.getCountry());
        userInfo.setProvince(goodNumberOrder.getProvince());
        userInfo.setCity(goodNumberOrder.getCity());
        userInfo.setDistrict(goodNumberOrder.getDistrict());
        userInfoLocalControllerFegin.addGoodNumberToUser(userInfo);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
