package com.busi.timerController;

import com.busi.entity.UserMembership;
import com.busi.servive.UserMembershipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.MqUtils;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 元老级会员满1年返现100元
 * @author: ZHaoJiaJie
 * @create: 2019-01-22 14:52
 */
@Slf4j
@Component
public class MemberCashbackTimerController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserMembershipService userMembershipService;

    @Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次
    public void cashbackMembership() {
        log.info("开始执行元老级会员满1年返现100元功能...");
        List list = userMembershipService.findMembershipList2();
        if (list != null && list.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                UserMembership membership = (UserMembership) list.get(j);
                if (membership != null) {
                    //更新数据库表
                    membership.setMemberShipLevelStatus(1);//更新返现状态
                    userMembershipService.update3(membership);
                    //更新缓存
                    Map<String, Object> userMembershipMap = CommonUtils.objectToMap(membership);
                    redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP + membership.getUserId(), userMembershipMap, Constants.USER_TIME_OUT);

                    //更新买家缓存、钱包、账单
                    mqUtils.sendPurseMQ(membership.getUserId(), 2, 0, 100);
                }
            }
        }
        log.info("动态处理了(" + list.size() + ")个满一年元老级会员...");
        log.info("动态处理元老级会员返现完毕...");
    }
}
