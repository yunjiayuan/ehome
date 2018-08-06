package com.busi.service;

import com.busi.entity.HouseNumber;
import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.HouseNumberControllerFegin;
import com.busi.adapter.MessageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册新用户 同步门牌号操作
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class UserService implements MessageAdapter {

    @Autowired
    private HouseNumberControllerFegin houseNumberControllerFegin;

    /***
     * fegin 调用login服务中的 更新门牌号操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            int proType = Integer.parseInt(body.getString("proType"));
            long houseNumber = Long.parseLong(body.getString("houseNumber"));
            HouseNumber hn = new HouseNumber();
            hn.setProKeyWord(proType);
            hn.setNewNumber(houseNumber);

            houseNumberControllerFegin.updateHouseNumber(hn);//fegin调用更新操作

            log.info("消息服务平台处理用户注册功能，同步门牌号记录表操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户注册功能，同步门牌号记录表操作失败，参数有误："+body.toJSONString());
        }
    }
}
