package com.busi.controller.api;

import com.busi.entity.Administrators;
import com.busi.entity.Community;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员相关接口
 * author：ZJJ
 * create time：2020-09-27 14:06:30
 */
public interface AdministratorsApiController {

    /***
     * 新增管理员
     * @param homeHospital
     * @return
     */
    @PostMapping("addAdministrator")
    ReturnData addAdministrator(@Valid @RequestBody Administrators homeHospital, BindingResult bindingResult);

    /***
     * 删除管理员
     * @param userId 管理员ID
     * @return:
     */
    @DeleteMapping("delAdministrator/{userId}")
    ReturnData delAdministrator(@PathVariable long userId);

    /***
     * 查询管理员列表
     * @param levels   级别：-1全部  0普通管理员 1高级管理员 2最高管理员
     * @return
     */
    @GetMapping("findAdministratorlist/{levels}")
    ReturnData findAdministratorlist(@PathVariable int levels);

    /***
     * 查询当前用户管理员权限
     * @return
     */
    @GetMapping("findAdministrator")
    ReturnData findAdministrator();
}
