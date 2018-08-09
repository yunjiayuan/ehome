package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 会员相关接口
 * author：SunTianJie
 * create time：2018/7/16 17:20
 */
public interface UserMembershipApiController {
    /***
     * 查询用户会员信息
     * @param userId 被查询者的用户ID
     * @return
     */
    @GetMapping("findUserMembershipInfo/{userId}")
    ReturnData findUserMembershipInfo(@PathVariable long userId);
}
