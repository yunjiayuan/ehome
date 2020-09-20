package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.RewardLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/***
 * 用户奖励记录相关接口
 * author：stj
 * create time：2019-3-6 16:30:31
 */
public interface RewardLogLocalController {

    /***
     * 新增
     * @param rewardLog
     * @return
     */
    @PostMapping("addRewardLog")
    ReturnData addRewardLog(@Valid @RequestBody RewardLog rewardLog);

    /***
     * 查询指定用户的今天的稿费作品奖励列表
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findRewardLogListByUserId/{userId}")
    List<RewardLog> findRewardLogListByUserId(@PathVariable(value="userId")  long userId);

}
