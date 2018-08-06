package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.adapter.MessageAdapter;
import com.busi.utils.SMSUtil;
import org.springframework.stereotype.Component;

/**
 * 发送短信具体业务
 * author suntj
 * Create time 2018/6/3 16:44
 */
@Component
public class PhoneService implements MessageAdapter {

    /***
     * 调用短信平台发送短信
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        //具体业务
        String phone = body.getString("phone");
        int phoneType = Integer.parseInt(body.getString("phoneType"));
        String phoneCode = body.getString("phoneCode");
        String content = "";//短信内容
        if(phoneType==0){//注册发送短信
            content = "云家园提醒您，您的手机注册验证码为："+phoneCode+",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
            SMSUtil.sendMessage(content, phone);
        }else{
            //预留
        }
    }
}
