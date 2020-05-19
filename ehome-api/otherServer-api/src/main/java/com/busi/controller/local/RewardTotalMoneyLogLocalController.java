package com.busi.controller.local;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardTotalMoneyLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 用户奖励总金额信息相关接口（内部服务器间调用）
 * author：stj
 * create time：2019-3-6 16:30:31
 */
public interface RewardTotalMoneyLogLocalController {

    /***
     * 修改奖励总金额记录 无记录时新增
     * @param rewardTotalMoneyLog
     * @return
     */
    @PostMapping("updateTotalRewardMoney")
    ReturnData updateTotalRewardMoney(@Valid @RequestBody RewardTotalMoneyLog rewardTotalMoneyLog);


    /***
     * 根据指定用户，查询获得奖励的总金额（服务器间调用）
     * @param userId
     * @return
     */
    @GetMapping("findTotalRewardMoneyInfo/{userId}")
    RewardTotalMoneyLog findTotalRewardMoneyInfo(@PathVariable(value = "userId") long userId);

}
