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
     * 查询安全中心数据接口
     * @param userId
     * @return
     */
    @GetMapping("findUserAccountSecurity/{userId}")
    ReturnData findUserAccountSecurity(@PathVariable long userId);

    /***
     * 绑定手机前，验证新手机号是否被占用接口
     * @param phone
     * @return
     */
    @GetMapping("checkNewPhone/{phone}")
    ReturnData checkNewPhone(@PathVariable String phone);

    /***
     * 绑定手机号接口
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("bindNewPhone")
    ReturnData bindNewPhone (@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 解绑手机号
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("unBindPhone")
    ReturnData unBindPhone (@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);


}
