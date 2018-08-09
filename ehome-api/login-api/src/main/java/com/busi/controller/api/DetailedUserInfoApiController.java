package com.busi.controller.api;

import com.busi.entity.DetailedUserInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 用户详细信息接口文档
 * author：SunTianJie
 * create time：2018/7/19 14:32
 */
public interface DetailedUserInfoApiController {

    /***
     * 查询用户详细信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @GetMapping("findDetailedUserInfo/{userId}")
    ReturnData findDetailedUserInfo(@PathVariable long userId);

    /***
     * 修改用户详细资料接口
     * @param detailedUserInfo
     * @return
     */
    @PutMapping("updateDetailedUserInfo")
    ReturnData updateDetailedUserInfo (@Valid @RequestBody DetailedUserInfo detailedUserInfo, BindingResult bindingResult);

}
