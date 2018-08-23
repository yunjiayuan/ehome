package com.busi.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息平台工具方法 目前只提供 更新钱包余额和交易明细 更新任务系统
 * author：SunTianJie
 * create time：2018/8/22 17:27
 */
@Component
public class MqUtils {

    @Autowired
    MqProducer mqProducer;

    /***
     * 更新钱包余额和钱包明细
     * @param userId          用户ID
     * @param tradeType       交易类型 0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出,18游戏支出，19游戏转入
     * @param currencyType    交易支付类型 0钱(真实人民币),1家币,2家点
     * @param tradeMoney      交易金额
     */
    public void sendPurseMQ(long userId,int tradeType,int currencyType,double tradeMoney){
        //调用MQ同步用户登录信息
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", 7);//interfaceType  7:表示更新钱包余额和钱包明细
        JSONObject content = new JSONObject();
        content.put("userId",userId);//用户ID
        content.put("tradeType",tradeType);
        content.put("currencyType",currencyType);
        content.put("tradeMoney",tradeMoney);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        mqProducer.sendMsg(activeMQQueue,sendMsg);
    }
}
