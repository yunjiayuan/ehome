package com.busi.controller.local;

import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 好友关系相关接口(fegin本地调用)
 * author：SunTianJie
 * create time：2018/7/16 17:20
 */
public interface UserRelationShipLocalApiController {

    /***
     * 获取好友列表接口
     * @param userId 被查询用户ID
     * @return
     */
    @GetMapping("findLocalFriendList/{userId}")
    List findLocalFriendList(@PathVariable(value="userId") long userId);

}
