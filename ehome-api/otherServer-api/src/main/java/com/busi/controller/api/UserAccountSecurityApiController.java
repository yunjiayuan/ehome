package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserAccountSecurity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 用户账户安全接口
 * author：SunTianJie
 * create time：2018/9/17 14:39
 */
public interface UserAccountSecurityApiController {


    /***
     * 新增安全中心数据接口
     * @param userAccountSecurity
     * @return
     */
    @PostMapping("addUserAccountSecurity")
    ReturnData addUserAccountSecurity(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 新增安全中心数据接口
     * @param userId
     * @return
     */
    @GetMapping("findUserAccountSecurity/{userId}")
    ReturnData findUserAccountSecurity(@PathVariable long userId);

    /***
     * 修改安全中心数据接口
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("updateUserAccountSecurity")
    ReturnData updateUserAccountSecurity (@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);


}
