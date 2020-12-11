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
     * 修改用户聊天互动状态
     * @param userInfo
     * @return
     */
    @PutMapping("updateChatnteractionStatus")
    ReturnData updateChatnteractionStatus (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 修改账号状态接口 启用、停用
     * @param userInfo
     * @return
     */
    @PutMapping("updateAccountStatus")
    ReturnData updateAccountStatus (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

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

    /***
     * 重置密码接口（用于其它方式修改和找回密码操作）
     * @param userInfo
     * @return
     */
    @PutMapping("resetPassWord")
    ReturnData resetPassWord (@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);

    /***
     * 找回密码验证账号是否存在
     * @param userAccount 门牌号组合 0_1001518
     * @param code        验证码
     * @return
     */
    @GetMapping("checkAccount/{userAccount}/{code}")
    ReturnData checkAccount(@PathVariable String userAccount,@PathVariable String code);

    /***
     * 完善资料界面中绑定已有门牌号
     * @param homeNumber           将要绑定的门牌号组合格式:0_1001518(目标门票号)
     * @param password             将要绑定的门牌号密码（一遍MD5加密后）
     * @param otherPlatformKey     当bindType=0时，此参数为第三方平台key ； 当bindType=1时，此参数为手机号
     * @param otherPlatformAccount 第三方平台昵称
     * @param otherPlatformType    第三方平台类型 1：QQ，2：微信
     * @param bindType             绑定类型 0表示手机号绑定门牌号  1表示第三方平台账号绑定门牌号
     * @return
     */
    @GetMapping("bindHouseNumber/{homeNumber}/{password}/{otherPlatformKey}/{otherPlatformAccount}/{otherPlatformType}/{bindType}")
    ReturnData bindHouseNumber(@PathVariable String homeNumber,@PathVariable String password,@PathVariable String otherPlatformKey,@PathVariable String otherPlatformAccount,
                          @PathVariable int otherPlatformType,@PathVariable int bindType);

    /***
     * 创建VIP账号、靓号、普通账号等预选账号接口（仅管理员可用）
     * @param userInfo
     * @return
     */
    @PostMapping("createVIPHouseNumber")
    ReturnData createVIPHouseNumber(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult);
//    /***
//     * 测试fegin被调用
//     * @param id
//     * @return
//     */
//    @GetMapping("testFegin/{id}")//最多只能有一个基本参数
//    ReturnData testFegin(@PathVariable("id") Integer id);
}
