package com.busi.controller.api;

import com.busi.entity.UserInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 新用户注册相关接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface RegisterApiController {

    /***
     * 门牌号注册接口
     * @param userInfo
     * @return
     */
    @PostMapping("registerByHouseNumber")
    ReturnData registerByHouseNumber(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 生成验证码
     * @return
     */
    @GetMapping("createCode/{type}/{phone}")
    ReturnData createCode(@PathVariable int type , @PathVariable String phone);

    /***
     * 校验服务端验证码
     * @return
     */
    @GetMapping("checkCode/{code}")
    ReturnData checkCode(@PathVariable String code);

    /***
     * 手机号注册接口
     * @param userInfo
     * @return
     */
    @PostMapping("registerByPhone")
    ReturnData registerByPhone(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 完善资料接口
     * @param userInfo
     * @return
     */
    @PutMapping("perfectUserInfo")
    ReturnData perfectUserInfo (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 查询用户基本信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @GetMapping("findUserInfo/{userId}")
    ReturnData findUserInfo(@PathVariable long userId);

    /***
     * 修改用户基本资料接口
     * @param userInfo
     * @return
     */
    @PutMapping("updateUserInfo")
    ReturnData updateUserInfo (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 更新用户头像接口
     * @param userInfo
     * @return
     */
    @PutMapping("updateUserHead")
    ReturnData updateUserHead (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 修改用户访问权限接口 停用
     * @param userInfo
     * @return
     */
    @PutMapping("updateUserAccessRights")
    ReturnData updateUserAccessRights (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 修改新用户系统欢迎消息状态接口
     * @return
     */
    @PutMapping("updateWelcomeInfoStatus")
    ReturnData updateWelcomeInfoStatus ();

    /***
     * 修改登录密码接口
     * @param userInfo
     * @return
     */
    @PutMapping("changePassWord")
    ReturnData changePassWord (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

//    /***
//     * 测试fegin被调用
//     * @param id
//     * @return
//     */
//    @GetMapping("testFegin/{id}")//最多只能有一个基本参数
//    ReturnData testFegin(@PathVariable("id") Integer id);
}
