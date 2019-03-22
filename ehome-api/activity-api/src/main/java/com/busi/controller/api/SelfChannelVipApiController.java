package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 自频道相关接口
 * author：zhaojiajie
 * create time：2019-3-20 15:53:38
 */
public interface SelfChannelVipApiController {

    /***
     * 查询用户是否是自频道会员
     * @param userId
     * @return
     */
    @GetMapping("rightVip/{userId}")
    ReturnData rightVip(@PathVariable long userId);
}
