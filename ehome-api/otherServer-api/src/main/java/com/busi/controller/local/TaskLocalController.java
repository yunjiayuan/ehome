package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.Task;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 任务相关接口(服务期间调用)
 * author：zhaojiajie
 * create time：2018-8-15 15:21:53
 */
public interface TaskLocalController {

    /***
     * 新增
     * @param task
     * @return
     */
    @PostMapping("addLocalTask")
    ReturnData addLocalTask(@RequestBody Task task);
}
