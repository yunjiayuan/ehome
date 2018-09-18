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
        if(phoneType==0) {//注册发送短信
            content = "云家园提醒您，您的手机注册验证码为：" + phoneCode + ",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
        }else if(phoneType==1){//支付密码找回 验证手机号
            content = "云家园提醒您，您的支付密码找回验证码为：" + phoneCode + ",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
        }else if(phoneType==2){//安全中心绑定手机验证码
            content = "云家园提醒您，您正在进行绑定手机操作，验证码为：" + phoneCode + ",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
        }else if(phoneType==3){//安全中心解绑手机验证码
            content = "云家园提醒您，您正在进行更换绑定手机验证操作，验证码为：" + phoneCode + ",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
        }else if(phoneType==4){//手机短信找回登录密码验证码
            content = "云家园提醒您，您正在进行找回登录密码操作，验证码为：" + phoneCode + ",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
        }else if(phoneType==5) {//手机短信修改密码验证码
            content = "云家园提醒您，您正在进行修改密码操作，验证码为：" + phoneCode + ",请您在10分钟之内使用，感谢您对云家园的关注与支持!";
        }else if(phoneType==6){//短信邀请新用户注册
                content = "云家园提醒您，您的朋友邀请您成为云家园新用户，邀请码为：" + phoneCode + ",最新云家园官方下载地址为：http://ephone.lichengwang.com/downLoad/index.html?shareCode=\"+code+\"，感谢您对历程网的关注与支持!";
        }else{
            //预留
        }
        //发送短信
        SMSUtil.sendMessage(content, phone);
    }
}
