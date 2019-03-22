package com.busi.controller.local;


import com.busi.entity.ReturnData;
import com.busi.entity.SelfChannelVip;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户自频道会员新增接口（内部调用）
 * author：ZHaoJiaJie
 * create time：2019-03-22 13:21
 */
public interface SelfChannelVipLocalController {

    /***
     * 新增会员信息
     * @param selfChannelVip
     * @return
     */
    @PostMapping("addSelfMember")
    ReturnData addSelfMember(@RequestBody SelfChannelVip selfChannelVip);
}
