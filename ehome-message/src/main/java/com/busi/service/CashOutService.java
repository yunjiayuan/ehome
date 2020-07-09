package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.CashOutLControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.CashOutOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 提现
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class CashOutService implements MessageAdapter {

    @Autowired
    private CashOutLControllerFegin cashOutLControllerFegin;

    /***
     * fegin 调用paymentServer服务中的 更新钱包余额操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            //将要变更的用户ID
            long userId = Long.parseLong(body.getString("userId"));
            int type = Integer.parseInt(body.getString("type"));
            String openId = body.getString("openId");
            Double tradeMoney = Double.parseDouble(body.getString("tradeMoney"));
            CashOutOrder cashOutOrder = new CashOutOrder();
            cashOutOrder.setUserId(userId);
            cashOutOrder.setType(type);
            cashOutOrder.setOpenid(openId);
            cashOutOrder.setMoney(tradeMoney);
            cashOutLControllerFegin.cashOutToOther(cashOutOrder);
            log.info("消息服务平台处理用户["+userId+"]提现操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户提现操作失败，参数有误："+body.toJSONString());
        }
    }
}
