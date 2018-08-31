package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 发送信息接口 例如发送短信验证码  发送邮件 发送消息等
 * author：SunTianJie
 * create time：2018-8-30 08:34:03
 */
public interface SendMessageApiController {
    /**
     * 发送手机短信
     * @param phone     将要发送短信的手机号
     * @param phoneType 短信类型 0注册验证码  1找回支付密码验证码 2安全中心绑定手机验证码 3安全中心解绑手机验证码
     *                            4手机短信找回登录密码验证码  5手机短信修改密码验证码 6短信邀请新用户注册 7...
     * @return
     */
    @GetMapping("SendPhoneMessage/{phone}/{phoneType}")
    ReturnData SendPhoneMessage(@PathVariable String phone, @PathVariable int phoneType);
}
