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

}
