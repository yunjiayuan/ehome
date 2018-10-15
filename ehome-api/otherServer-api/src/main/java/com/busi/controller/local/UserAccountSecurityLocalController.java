package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.UserAccountSecurity;
import org.springframework.web.bind.annotation.*;

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

    /***
     * 更新安全中心信息（目前只提供手机和第三方注册新用户时同步安全中心信息和完善资料时绑定门牌号时使用）
     * @param userAccountSecurity
     * @return
     */
    @PostMapping("addAccountSecurity")
    ReturnData addAccountSecurity(@RequestBody UserAccountSecurity userAccountSecurity);
}
