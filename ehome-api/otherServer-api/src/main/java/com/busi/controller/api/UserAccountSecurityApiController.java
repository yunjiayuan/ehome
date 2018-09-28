package com.busi.controller.api;

import com.busi.entity.RealNameInfo;
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
     * 查询安全中心详情接口
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
     * 短信验证修改密码前，验证短信验证码是否正确接口
     * @param code
     * @param checkType 0修改密码验证旧手机 1找回密码验证旧手机
     * @return
     */
    @GetMapping("checkPhoneCode/{code}/{checkType}")
    ReturnData checkPhoneCode(@PathVariable String code,@PathVariable int checkType);

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

    /***
     * 查询密保问题信息接口
     * @param userId
     * @return
     */
    @GetMapping("findQuestion/{userId}")
    ReturnData findQuestion(@PathVariable long userId);

    /***
     * 验证密保问题信息
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("checkQuestion")
    ReturnData checkQuestion(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 设置和修改密保问题信息
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("addQuestion")
    ReturnData addQuestion(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 实名认证接口
     * @return
     */
    @PutMapping("checkRealName")
    ReturnData checkRealName(@Valid @RequestBody RealNameInfo realNameInfo, BindingResult bindingResult);

    /***
     * 门牌号绑定微信、QQ、新浪微博等第三方平台账号
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("bindHouseNumber")
    ReturnData bindHouseNumber(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 解除门牌号与第三方平台账号（微信、QQ、新浪微博）之间的绑定关系
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("unBindHouseNumber")
    ReturnData unBindHouseNumber(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 绑定邮箱前，验证新邮箱是否被占用接口
     * @param email
     * @return
     */
    @GetMapping("checkNewEmail/{email}")
    ReturnData checkNewEmail(@PathVariable String email);

    /***
     * 邮箱验证修改密码前，验证邮箱验证码是否正确接口
     * @param code
     * @param checkType
     * @return
     */
    @GetMapping("checkEmailCode/{code}/{checkType}")
    ReturnData checkEmailCode(@PathVariable String code,@PathVariable int checkType);

    /***
     * 绑定邮箱接口
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("bindNewEmail")
    ReturnData bindNewEmail (@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

    /***
     * 解绑邮箱
     * @param userAccountSecurity
     * @return
     */
    @PutMapping("unBindEmail")
    ReturnData unBindEmail (@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult);

}
