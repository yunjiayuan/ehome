package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 钱包相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
public interface PurseApiController {

    /***
     * 查询用户钱包信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @GetMapping("findPurseInfo/{userId}")
    ReturnData findPurseInfo(@PathVariable long userId);

    /***
     * 查询互动用户双方钱包家点信息
     * @param myId     当前登录用户ID
     * @param userId   好友用户ID
     * @return
     */
    @GetMapping("findHomePointInfo/{myId}/{userId}")
    ReturnData findHomePointInfo(@PathVariable long myId,@PathVariable long userId);

    /**
     * 钱包兑换接口
     * @param userId       当前用户ID
     * @param exChangeType 兑换类型 1家币兑换家点（1:100） 2人民币兑换家币（1:1）
     * @param money        当exChangeType==1时人民币兑换家币 当exChangeType==2时家币兑换家点
     * @return
     */
    @GetMapping("exChange/{userId}/{exChangeType}/{money}")
    ReturnData exChange(@PathVariable long userId,@PathVariable int exChangeType,@PathVariable long money);

}
