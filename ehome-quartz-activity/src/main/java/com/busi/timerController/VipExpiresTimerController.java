package com.busi.timerController;

import com.busi.entity.SelfChannelVip;
import com.busi.servive.SelfChannelVipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 自频道到期会员
 * @author: ZHaoJiaJie
 * @create: 2019-08-19 10:27
 */
@Slf4j
@Component
public class VipExpiresTimerController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    SelfChannelVipService selfChannelVipService;

    @Scheduled(cron = "0 5 1 * * ?") // 每秒执行一次
    public void membershipExpiresTimer() throws Exception {
        log.info("开始处理自频道到期会员...");
        long nowTime = new Date().getTime();// 系统时间
        //处理自频道会员
        List selfList = selfChannelVipService.findMembershipList();
        if (selfList != null && selfList.size() > 0) {
            for (int j = 0; j < selfList.size(); j++) {
                SelfChannelVip vip = (SelfChannelVip) selfList.get(j);
                if (vip != null) {
                    if (nowTime <= vip.getExpiretTime().getTime()) {
                        vip.setMemberShipStatus(1);
                        selfChannelVipService.update(vip);
                        //更新缓存
                        Map<String, Object> userMembershipMap = CommonUtils.objectToMap(vip);
                        redisUtils.hmset(Constants.REDIS_KEY_SELFCHANNELVIP + vip.getUserId(), userMembershipMap, Constants.USER_TIME_OUT);
                    }
                }
            }
        }
        log.info("动态处理到期会员完毕...");
    }
}
