package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.LoginStatusInfoControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.LoginStatusInfo;
import com.busi.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 同步登录信息
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class LoginStatusInfoService implements MessageAdapter {

    @Autowired
    private LoginStatusInfoControllerFegin loginStatusInfoControllerFegin;

    /***
     * fegin 调用login服务中的 新增用户登录信息接口
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            String clientInfo = body.getString("clientInfo");
            long userId = Long.parseLong(body.getString("userId"));
            String ip = body.getString("ip");
            LoginStatusInfo loginStatusInfo = new LoginStatusInfo();
            loginStatusInfo.setUserId(userId);
            loginStatusInfo.setIp(ip);
            loginStatusInfo.setTime(new Date());
            String[] array = null;
            if(!CommonUtils.checkFull(clientInfo)){
                array = clientInfo.split("/");
            }
            if(array!=null&&array.length==5){
                loginStatusInfo.setClientType(array[0]);
                loginStatusInfo.setClientModel(array[1]);
                loginStatusInfo.setClientSystemModel(array[2]);
                loginStatusInfo.setServerVersion(array[3]);
                loginStatusInfo.setAppVersion(array[4]);
            }else{
                loginStatusInfo.setClientType(clientInfo);
                loginStatusInfo.setClientModel(clientInfo);
                loginStatusInfo.setClientSystemModel(clientInfo);
                loginStatusInfo.setServerVersion(clientInfo);
                loginStatusInfo.setAppVersion(clientInfo);
            }

            loginStatusInfoControllerFegin.addLoginStatusInfo(loginStatusInfo);//fegin调用新增操作
            log.info("消息服务平台处理用户登录时，同步用户登录状态信息记录表操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户登录时，同步用户登录状态信息记录表操作失败，参数有误："+body.toJSONString());
        }
    }
}
