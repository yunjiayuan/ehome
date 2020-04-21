package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.LookLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.Look;
import com.busi.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 公告系统同步浏览量
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class IpsService implements MessageAdapter {

    @Autowired
    private LookLocalControllerFegin lookLocalControllerFegin;


    /***
     * fegin 调用ips服务中的 更新浏览量操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            //将要变更的用户ID
            long myId = Long.parseLong(body.getString("myId"));
            long infoId = Long.parseLong(body.getString("infoId"));
            int afficheType = Integer.parseInt(body.getString("afficheType"));
            String title = body.getString("title");
            if(myId<=0||afficheType<0||afficheType>10||CommonUtils.checkFull(title)){
                log.info("消息服务平台处理新增IPS系统公告浏览量操作失败，参数有误："+body.toJSONString());
                return;
            }
            Look look =  new Look();
            look.setMyId(myId);
            look.setInfoId(infoId);
            look.setAfficheType(afficheType);
            look.setTitle(title);
            lookLocalControllerFegin.addLook(look);
            log.info("消息服务平台处理新增IPS系统公告浏览量操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理新增IPS系统公告浏览量操作失败，参数有误："+body.toJSONString());
        }
    }
}
