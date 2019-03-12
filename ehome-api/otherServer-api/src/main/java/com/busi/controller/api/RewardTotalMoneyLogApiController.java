package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.RewardTotalMoneyLog;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 用户奖励总金额信息接口
 * author：stj
 * create time：2019-3-6 16:30:31
 */
public interface RewardTotalMoneyLogApiController {

    /***
     * 根据指定用户，查询获得奖励的总金额
     * @param userId
     * @return
     */
    @GetMapping("findTotalRewardMoney/{userId}")
    ReturnData findTotalRewardMoney(@PathVariable long userId);

    /***
     * 将指定金额的奖励转入钱包
     * @param rewardTotalMoneyLog
     * @param bindingResult
     * @return
     */
    @PostMapping("rewardMoneyToPurse")
    ReturnData rewardMoneyToPurse(@Valid @RequestBody RewardTotalMoneyLog rewardTotalMoneyLog, BindingResult bindingResult);

}
