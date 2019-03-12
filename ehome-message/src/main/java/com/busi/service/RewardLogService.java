package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.RewardLogLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.RewardLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 奖励记录service
 * author suntj
 * Create time 2019-3-7 10:50:13
 */
@Component
@Slf4j
public class RewardLogService implements MessageAdapter {

    @Autowired
    private RewardLogLocalControllerFegin rewardLogLocalControllerFegin;

    /***
     * fegin 调用otherServer服务中的 新增奖励记录
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            //用户ID
            long userId = Long.parseLong(body.getString("userId"));
            long infoId = Long.parseLong(body.getString("infoId"));
            //奖励类型 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
            int rewardType = Integer.parseInt(body.getString("rewardType"));
            int rewardMoneyType = Integer.parseInt(body.getString("rewardMoneyType"));
            double rewardMoney = Double.parseDouble(body.getString("rewardMoney"));
            RewardLog rewardLog = new RewardLog();
            rewardLog.setUserId(userId);
            rewardLog.setInfoId(infoId);
            rewardLog.setRewardType(rewardType);
            rewardLog.setRewardMoneyType(rewardMoneyType);
            rewardLog.setRewardMoney(rewardMoney);
            rewardLogLocalControllerFegin.addRewardLog(rewardLog);
            log.info("消息服务平台处理用新增用户奖励记录操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用新增用户奖励记录操作失败，参数有误："+body.toJSONString());
        }
    }
}
