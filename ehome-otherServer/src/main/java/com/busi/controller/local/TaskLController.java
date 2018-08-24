package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.Task;
import com.busi.entity.TaskList;
import com.busi.service.TaskService;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;

/***
 * 任务相关接口(服务期间调用)
 * author：zhaojiajie
 * create time：2018-8-15 15:21:53
 */
@RestController
public class TaskLController extends BaseController implements TaskLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TaskService taskService;

    /***
     * 新增
     * @param task
     * @return
     */
    @Override
    public ReturnData addLocalTask(@RequestBody Task task) {
        //查询缓存 缓存中不存在 查询数据库（是否已完成）
        List list = redisUtils.getList(Constants.REDIS_KEY_IPS_TASK + task.getUserId(), 0, -1);
        if (list == null || list.size() <= 0) {
            Task task2 = null;
            task2 = taskService.findUserById(task.getUserId(), task.getTaskType(), task.getSortTask());
            if (task2 == null) {
                task.setTime(new Date());
                task.setTaskStatus(1);
                taskService.add(task);
            } else {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该任务已完成", new JSONObject());
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                TaskList t = (TaskList) list.get(i);
                if (t.getTaskType() == task.getTaskType() && t.getTaskId() == task.getSortTask()) {
                    if (t.getTaskStatus() == 0) {
                        //新增
                        task.setTime(new Date());
                        task.setTaskStatus(1);
                        taskService.add(task);
                    } else {
                        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该任务已经完成", new JSONObject());
                    }
                    break;
                }
            }
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_TASK + task.getUserId() + task.getTaskType() + task.getSortTask(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
