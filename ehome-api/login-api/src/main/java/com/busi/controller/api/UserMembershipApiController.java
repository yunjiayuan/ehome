package com.busi.controller.api;

import com.busi.entity.MemberOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

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

    /***
     * 购买会员下单接口
     * @param memberOrder
     * @return
     */
    @PostMapping("addMemberOrder")
    ReturnData addMemberOrder(@Valid @RequestBody MemberOrder memberOrder, BindingResult bindingResult);
}
