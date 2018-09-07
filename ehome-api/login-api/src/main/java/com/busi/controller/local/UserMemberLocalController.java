package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.UserMembership;
import org.springframework.web.bind.annotation.*;

/**
 * 用户会员新增和更新接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface UserMemberLocalController {

    /***
     * 新增会员信息
     * @param userMembership
     * @return
     */
    @PostMapping("addUserMember")
    ReturnData addUserMember(@RequestBody UserMembership userMembership);

    /***
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @PutMapping("updateUserMember")
    ReturnData updateUserMember(@RequestBody UserMembership userMembership);

    /***
     * 查询用户会员信息
     * @param userId
     * @return
     */
    @GetMapping("getUserMember/{userId}")
    UserMembership getUserMember(@PathVariable(value="userId") long userId);

}
