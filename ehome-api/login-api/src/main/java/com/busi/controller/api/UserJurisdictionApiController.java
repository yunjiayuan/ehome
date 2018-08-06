package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserJurisdiction;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 用户权限相关接口（设置功能中的权限设置 包括房间锁和被访问权限）
 * author：SunTianJie
 * create time：2018/7/30 10:45
 */
public interface UserJurisdictionApiController {

    /***
     * 修改权限
     * @param userJurisdiction
     * @return
     */
    @PutMapping("updateUserJurisdiction")
    ReturnData updateUserJurisdiction(@Valid @RequestBody UserJurisdiction userJurisdiction, BindingResult bindingResult);

    /***
     * 查询权限信息
     * @return
     */
    @GetMapping("findUserJurisdiction/{userId}")
    ReturnData findUserJurisdiction(@PathVariable long userId);
}
