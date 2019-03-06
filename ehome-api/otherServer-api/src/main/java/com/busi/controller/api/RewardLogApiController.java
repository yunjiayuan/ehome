package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 用户奖励记录相关接口
 * author：stj
 * create time：2019-3-6 16:30:31
 */
public interface RewardLogApiController {

    /***
     * 查询指定用户的奖励列表
     * @param userId  用户ID
     * @param rewardType  奖励类型 -1所以 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
     * @return
     */
    @GetMapping("findRewardLogList/{userId}/{rewardType}/{page}/{count}")
    ReturnData findRewardLogList(@PathVariable long userId, @PathVariable int rewardType, @PathVariable int page, @PathVariable int count);

}
