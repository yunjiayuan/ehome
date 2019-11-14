package com.busi.timerController;

import com.busi.entity.UserMembership;
import com.busi.servive.UserMembershipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 会员到期
 * @author: ZHaoJiaJie
 * @create: 2019-01-22 13:19
 */
@Slf4j
@Component
public class MembershipExpiresTimerController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserMembershipService userMembershipService;

    @Scheduled(cron = "0 55 2 * * ?") // 每秒执行一次
    public void membershipExpiresTimer() throws Exception {
        log.info("开始处理到期会员...");
        int memberShipStatus = 0;//用户当前会员状态  1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
        long regularExpireTime = 0;//普通会员到期时间
        long nowTime = new Date().getTime();// 系统时间
        List<Object> usedList = new ArrayList<Object>();
        //处理高级会员
        List list = userMembershipService.findMembershipList();
        if (list != null && list.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                UserMembership membership = (UserMembership) list.get(j);
                if (membership != null) {
                    memberShipStatus = membership.getMemberShipStatus();
                    if (memberShipStatus == 1) {//普通会员
                        regularExpireTime = membership.getRegularExpireTime().getTime();//普通会员到期时间
                        if (regularExpireTime <= nowTime) {
                            membership.setMemberShipStatus(0);
                            membership.setRegularMembershipLevel(0);
                            //更新数据库
                            userMembershipService.update2(membership);
                            usedList.add(membership);
                            //更新缓存
                            Map<String, Object> userMembershipMap = CommonUtils.objectToMap(membership);
                            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP + membership.getUserId(), userMembershipMap, Constants.USER_TIME_OUT);
                        }
                    } else {//vip高级会员
                        long vipExpireTime = membership.getVipExpireTime().getTime();//vip高级会员到期时间
                        if (vipExpireTime <= nowTime) {
                            //判断是不是普通会员
                            if (membership.getRegularMembershipLevel() == 1) {//是
                                //更新普通会员到期时间
                                long regularStopTime = membership.getRegularStopTime().getTime();//普通会员临时终止时间
                                regularExpireTime -= regularStopTime;
                                regularExpireTime += nowTime;
                                Date now = new Date(regularExpireTime);//新的到期时间

                                membership.setRegularExpireTime(now);
                                membership.setMemberShipStatus(1);//用户当前会员状态  1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
                                membership.setVipMembershipLevel(0);//VIP高级会员等级  0：默认非会员 1：一级高级会员
                            } else {//不是
                                membership.setMemberShipStatus(0);
                                membership.setVipMembershipLevel(0);
                            }
                            //更新缓存
                            Map<String, Object> userMembershipMap = CommonUtils.objectToMap(membership);
                            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP + membership.getUserId(), userMembershipMap, Constants.USER_TIME_OUT);
                            //更新数据库
                            userMembershipService.update(membership);
                            usedList.add(membership);
                        }
                    }
                }
            }
        }
        log.info("动态处理了(" + usedList.size() + ")个到期会员...");
        log.info("动态处理到期会员完毕...");
    }
}
