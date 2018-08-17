package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.controller.TaskApiController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.Task;
import com.busi.entity.TaskList;
import com.busi.service.TaskService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * @program: 任务
 * @author: ZHaoJiaJie
 * @create: 2018-08-16 14:23
 */
@RestController
public class TaskController extends BaseController implements TaskApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TaskService taskService;

    /***
     * 新增任务
     * @param task
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTask(@Valid Task task, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库（是否已完成）
        Map<String, Object> taskMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_TASK + task.getUserId() + task.getTaskType() + task.getSortTask());
        if (taskMap == null || taskMap.size() <= 0) {
            Task task2 = null;
            task2 = taskService.findUserById(task.getUserId(), task.getTaskType(), task.getSortTask());
            if (task2 == null) {
                task.setTaskStatus(1);
                task.setTime(new Date());
                taskService.add(task);
                //放入缓存
                taskMap = CommonUtils.objectToMap(task);
                redisUtils.hmset(Constants.REDIS_KEY_IPS_TASK + task.getUserId() + task.getTaskType() + task.getSortTask(), taskMap, Constants.USER_TIME_OUT);
            } else {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该任务已完成", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该任务已完成", new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_TASK + task.getUserId() + task.getTaskType() + task.getSortTask(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询任务列表
     * @param userId  用户ID
     * @param taskType  任务类型：0、一次性任务   1 、每日任务
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findTaskList(long userId, int taskType, int page, int count) {
        //验证参数
        if (taskType < 0 || taskType > 3) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数taskType有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<Task> pageBean;//任务
        PageBean<TaskList> pageBeanList;//任务详情
        pageBean = taskService.findList(userId, taskType, page, count);
        pageBeanList = taskService.findTaskList(page, count);
        if (pageBeanList != null && pageBeanList.getSize() > 0) {
            if (pageBean != null && pageBean.getSize() > 0) {
                for (int i = 0; i < pageBean.getSize(); i++) {
                    Task t = pageBean.getList().get(i);
                    for (int j = 0; j < pageBeanList.getSize(); j++) {
                        TaskList task = pageBeanList.getList().get(j);
                        if (t.getTaskType() == task.getTaskType()) {
                            if (t.getSortTask() == task.getTaskId()) {
                                task.setTaskStatus(t.getTaskStatus());
                            }
                        }
                    }
                }
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBeanList);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
    }

    /***
     * 更改任务状态（领奖）
     * @param sortTask  任务id
     * @param userId  用户ID
     * @param taskType  任务类型：0、一次性任务   1 、每日任务
     * @return
     */
    @Override
    public ReturnData updateTaskState(long sortTask, long userId, int taskType) {
        //验证参数
        if (sortTask <= 0 || userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        if (taskType < 0 || taskType > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的任务信息", new JSONObject());
        }
        Task task = taskService.findUserById(sortTask, userId, taskType);
        if (task == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (task.getTaskStatus() == 1) {
            task.setTaskStatus(2);
            taskService.update(task);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + task.getUserId() + task.getTaskType() + task.getSortTask(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 公共方法 新增任务
     *
     * @param userId   用户ID
     * @param sortTask 任务ID
     * @param taskType 任务类型：0、一次性任务   1 、每日任务
     * @return
     */
    public boolean add(long userId, int sortTask, int taskType) {

        if (userId <= 0 || sortTask < 0 || taskType < 0) {
            return false;
        }
        //查询数据库（是否已完成）
        Task task = new Task();
        task = taskService.findUserById(task.getUserId(), task.getTaskType(), task.getSortTask());
        if (task != null) {
            return false;
        }
        task.setUserId(userId);
        task.setTaskType(taskType);
        task.setTaskStatus(1);
        task.setSortTask(sortTask);
        task.setTime(new Date());
        taskService.add(task);

        //放入缓存
        Map<String, Object> taskMap = CommonUtils.objectToMap(task);
        redisUtils.hmset(Constants.REDIS_KEY_IPS_TASK + task.getUserId() + task.getTaskType() + task.getSortTask(), taskMap, Constants.USER_TIME_OUT);

        return true;
    }
}
