package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.adapter.MessageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 任务系统
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class TaskService implements MessageAdapter {


    /***
     * fegin 调用otherServer服务中的 新增任务操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {


            log.info("消息服务平台处理用新增任务操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用新增任务操作失败，参数有误："+body.toJSONString());
        }
    }
}
