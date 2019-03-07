package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.RewardLogLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.controller.local.RewardTotalMoneyLogLocalController;
import com.busi.entity.RewardLog;
import com.busi.entity.RewardTotalMoneyLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 奖励总金额service
 * author suntj
 * Create time 2019-3-7 10:50:13
 */
@Component
@Slf4j
public class RewardTotalMoneyLogService implements MessageAdapter {

    @Autowired
    private RewardTotalMoneyLogLocalController rewardTotalMoneyLogLocalController;

    /***
     * fegin 调用otherServer服务中的 更新奖励总金额
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            //用户ID
            long userId = Long.parseLong(body.getString("userId"));
            //奖励类型 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
            double rewardTotalMoney = Double.parseDouble(body.getString("rewardTotalMoney"));
            RewardTotalMoneyLog rewardTotalMoneyLog = new RewardTotalMoneyLog();
            rewardTotalMoneyLog.setUserId(userId);
            rewardTotalMoneyLog.setRewardTotalMoney(rewardTotalMoney);
            rewardTotalMoneyLogLocalController.updateTotalRewardMoney(rewardTotalMoneyLog);
            log.info("消息服务平台处理用更新用户奖励总金额操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用新增用户奖励总金额操作失败，参数有误："+body.toJSONString());
        }
    }
}
