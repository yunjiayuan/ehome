package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.adapter.MessageAdapter;
import org.springframework.stereotype.Component;

/**
 * 发送邮件具体业务
 * author suntj
 * Create time 2018/6/3 16:43
 */
@Component
public class EmailService implements MessageAdapter {

    /**
     * 发送邮件
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        //具体业务
    }
}
