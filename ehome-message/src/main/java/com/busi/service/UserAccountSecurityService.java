package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.UserAccountSecurityLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.UserAccountSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 安全中心系统
 * author suntj
 * Create time 2018-10-11 14:04:43
 */
@Component
@Slf4j
public class UserAccountSecurityService implements MessageAdapter {

    @Autowired
    private UserAccountSecurityLocalControllerFegin userAccountSecurityLocalControllerFegin;

    /***
     * fegin 调用otherServer服务中的 新增会员中心数据操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            String phone = body.getString("phone");//密保手机(绑定手机，由于手机登录为同一个手机号)
            int otherPlatformType = Integer.parseInt(body.getString("otherPlatformType"));//是否绑定第三方平台账号，0：未绑定, 1：绑定QQ账号，2：绑定微信账号，3：绑定新浪微博账号
            String otherPlatformAccount = body.getString("otherPlatformAccount");//第三方平台账号名称
            String otherPlatformKey = body.getString("otherPlatformKey");//第三方平台账号key
            UserAccountSecurity userAccountSecurity = new UserAccountSecurity();
            userAccountSecurity.setUserId(userId);
            userAccountSecurity.setPhone(phone);
            userAccountSecurity.setOtherPlatformKey(otherPlatformKey);
            userAccountSecurity.setOtherPlatformAccount(otherPlatformAccount);
            userAccountSecurity.setOtherPlatformType(otherPlatformType);
            userAccountSecurityLocalControllerFegin.addAccountSecurity(userAccountSecurity);
            log.info("消息服务平台处理新用户注册新增安全中心信息操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理新用户注册新增安全中心信息操作失败，参数有误："+body.toJSONString());
        }
    }
}
