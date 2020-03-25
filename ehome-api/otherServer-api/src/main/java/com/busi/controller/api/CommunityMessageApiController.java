package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 居委会、物业消息相关接口
 * author：ZHaoJiaJie
 * create time：2020-03-24 13:53:47
 */
public interface CommunityMessageApiController {

    /***
     * 查询消息列表
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     用户ID
     * @param type     查询类型  0所有 1未读 2已读
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findMessageList/{communityType}/{communityId}/{type}/{userId}/{page}/{count}")
    ReturnData findMessageList(@PathVariable int communityType, @PathVariable int communityId, @PathVariable int type, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 获取未读消息
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     查询用户ID
     * @return
     */
    @GetMapping("getMessageCount/{communityType}/{communityId}/{userId}")
    ReturnData getMessageCount(@PathVariable int communityType, @PathVariable int communityId, @PathVariable long userId);
}
