package com.busi.controller.local;

import com.busi.entity.UserAccountSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户账户安全信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/9/18 18:29
 */
public interface UserAccountSecurityLocalController {

    /***
     * 查询用户安全信息
     * @param userId
     * @return
     */
    @GetMapping("getUserAccountSecurity/{userId}")
    UserAccountSecurity getUserAccountSecurity(@PathVariable(value="userId") long userId);
}
