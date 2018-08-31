package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.TaskService;
import com.busi.service.UserMembershipService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
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

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserMembershipService userMembershipService;

    /***
     * 新增任务
     * @param task
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTask(@Valid @RequestBody Task task, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库（是否已完成）
        List list = redisUtils.getList(Constants.REDIS_KEY_IPS_TASK + task.getUserId(), 0, -1);
        if (list == null || list.size() <= 0) {
            Task task2 = null;
            task2 = taskService.findUserById(task.getUserId(), task.getTaskType(), task.getSortTask());
            if (task2 == null) {
                task.setTaskStatus(1);
                task.setTime(new Date());
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
                        task.setTaskStatus(1);
                        task.setTime(new Date());
                        taskService.add(task);
                    } else {
                        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该任务已完成", new JSONObject());
                    }
                    break;
                }
            }
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_TASK + task.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询任务列表
     * @param userId  用户ID
     * @param taskType  任务类型：-1默认不限 0、一次性任务   1 、每日任务
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findTaskList(@PathVariable long userId, @PathVariable int taskType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (taskType < -1 || taskType > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数taskType有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //查询缓存
        if (taskType == -1) {
            List list = redisUtils.getList(Constants.REDIS_KEY_IPS_TASK + userId, 0, -1);
            if (list != null && list.size() > 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
            }
        }
        //开始查询
        PageBean<Task> pageBean;//任务
        PageBean<TaskList> pageBeanList;//任务详情
        pageBean = taskService.findList(userId, taskType, page, count);
        pageBeanList = taskService.findTaskList(taskType, page, count);
        List task = pageBean.getList();
        List taskList = pageBeanList.getList();
        if (taskList != null && taskList.size() > 0) {
            if (task != null && task.size() > 0) {
                for (int i = 0; i < task.size(); i++) {
                    Task t = (Task) task.get(i);
                    for (int j = 0; j < taskList.size(); j++) {
                        TaskList taskLi = (TaskList) taskList.get(j);
                        if (t.getTaskType() == taskLi.getTaskType()) {
                            if (t.getSortTask() == taskLi.getTaskId()) {
                                taskLi.setTaskStatus(t.getTaskStatus());
                            }
                        }
                    }
                }
                if (taskType == -1) {
                    //更新缓存
                    redisUtils.pushList(Constants.REDIS_KEY_IPS_TASK + userId, taskList, CommonUtils.getCurrentTimeTo_12());
                }
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, taskList);
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
    public ReturnData updateTaskState(@PathVariable long userId, @PathVariable int taskType, @PathVariable long sortTask) {
        //验证参数
        if (sortTask < 0 || userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        if (taskType < 0 || taskType > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + userId + "]无权限修改用户[" + userId + "]的任务信息", new JSONObject());
        }
        Task task = taskService.findUserById(userId, taskType, sortTask);
        if (task == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该用户任务异常", new JSONObject());
        }
        if (task.getTaskStatus() == 1) {
            //获取会员等级 根据用户会员等级获得相应家点
            int homePoint = 20;
            int memberShipStatus = 0;
            Map<String, Object> memberMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP + userId);
            if (memberMap == null || memberMap.size() <= 0) {
                //缓存中没有用户对象信息 查询数据库
                UserMembership userMembership = userMembershipService.findUserMembership(userId);
                if (userMembership == null) {
                    userMembership = new UserMembership();
                    userMembership.setUserId(userId);
                } else {
                    userMembership.setRedisStatus(1);//数据库中已有对应记录
                }
                memberMap = CommonUtils.objectToMap(userMembership);
                //更新缓存
                redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP + userId, memberMap, Constants.USER_TIME_OUT);
            }
            if (memberMap.get("memberShipStatus") != null && !CommonUtils.checkFull(memberMap.get("memberShipStatus").toString())) {
                memberShipStatus = Integer.parseInt(memberMap.get("memberShipStatus").toString());
                if (memberShipStatus == 1) {//普通会员
                    homePoint += homePoint * 0.1;
                } else if (memberShipStatus > 1) {//高级以上
                    homePoint += homePoint * 0.5;
                }
            }
            //更新钱包余额
            mqUtils.sendPurseMQ(userId, 20, 2, homePoint);

            //更新任务数据库
            task.setTaskStatus(2);
            taskService.update(task);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_TASK + task.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

}
