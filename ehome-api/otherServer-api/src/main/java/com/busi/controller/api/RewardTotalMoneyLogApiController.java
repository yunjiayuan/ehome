package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

}
