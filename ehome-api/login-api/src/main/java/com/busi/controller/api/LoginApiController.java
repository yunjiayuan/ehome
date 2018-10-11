package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户登录和注销接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface LoginApiController {

    /***
     * 门牌号登录接口
     * @param account   省简称ID_门牌号、手机号、或第三方平台登录类型   格式 0_1001518 或15901213694 或 1
     * @param password  登录密码(一遍32位MD5加密后的密码)或第三方平台登录key
     * @param loginType 登录类型 0门牌号登录 1手机号登录 2第三方平台账号登录
     * @param otherPlatformAccount 第三方平台名字 用于同步安全中心
     * @return
     */
    @GetMapping("login/{loginType}/{account}/{password}/{otherPlatformAccount}")
    ReturnData login(@PathVariable int loginType,@PathVariable String account , @PathVariable String password, @PathVariable String otherPlatformAccount);

    /***
     * 退出登录接口
     * @param myId 当前登录者的用户ID
     * @return
     */
    @GetMapping("loginOut/{myId}")
    ReturnData loginOut(@PathVariable long myId);


}
