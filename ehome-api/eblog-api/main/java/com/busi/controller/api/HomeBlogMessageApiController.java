package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 生活圈消息相关接口
 * author：ZHaoJiaJie
 * create time：22018-11-8 10:27:52
 */
public interface HomeBlogMessageApiController {

    /***
     * 查询生活圈消息接口
     * @param userId     用户ID
     * @param type     查询类型  0所有 1未读 2已读
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findMessageList/{type}/{userId}/{page}/{count}")
    ReturnData findMessageList(@PathVariable int type, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 获取未读消息 和关注数量 和粉丝数量
     * @param userId     查询用户ID
     * @return
     */
    @GetMapping("getMessageCount/{userId}")
    ReturnData getMessageCount(@PathVariable long userId);

}
