package com.busi.controller;

import com.busi.entity.ReturnData;
import com.busi.entity.Task;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/***
 * 任务相关接口
 * author：zhaojiajie
 * create time：2018-8-15 15:21:53
 */
public interface TaskApiController {

    /***
     * 新增
     * @param task
     * @param bindingResult
     * @return
     */
    @PostMapping("addTask")
    ReturnData addTask(@Valid @RequestBody Task task, BindingResult bindingResult);


    /***
     * 分页查询
     * @param userId  用户ID
     * @param taskType  任务类型：0、一次性任务   1 、每日任务
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findTaskList/{userId}/{taskType}/{page}/{count}")
    ReturnData findTaskList(@PathVariable long userId,@PathVariable int taskType, @PathVariable int page, @PathVariable int count);

    /**
     * 更新任务状态
     * @param id
     * @param userId
     * @param taskType  任务类型：0、一次性任务   1 、每日任务
     * @return
     */
    @GetMapping("updateTaskState/{id}/{userId}/{taskType}")
    ReturnData updateTaskState(@PathVariable long id, @PathVariable long userId, @PathVariable int taskType);
}
