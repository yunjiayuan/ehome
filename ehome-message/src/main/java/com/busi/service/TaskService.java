package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.TaskControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务系统
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class TaskService implements MessageAdapter {

    @Autowired
    private TaskControllerFegin taskControllerFegin;

    /***
     * fegin 调用otherServer服务中的 新增任务操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            //用户ID
            long userId = Long.parseLong(body.getString("userId"));
            //任务类型 0、一次性任务   1 、每日任务
            int taskType = Integer.parseInt(body.getString("taskType"));
            //任务ID
            int sortTask = Integer.parseInt(body.getString("sortTask"));
            Task task = new Task();
            task.setUserId(userId);
            task.setTaskType(taskType);
            task.setSortTask(sortTask);
            taskControllerFegin.addLocalTask(task);
            log.info("消息服务平台处理用新增任务操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用新增任务操作失败，参数有误："+body.toJSONString());
        }
    }
}
