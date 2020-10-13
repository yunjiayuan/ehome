package com.busi.timerController;

import com.busi.entity.UserInfo;
import com.busi.entity.VisitView;
import com.busi.servive.UserInfoService;
import com.busi.servive.VisitViewService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @program: ehome
 * @description: 随机新增访问量，每日给用户新增一些访问量
 * @author: ZHaoJiaJie
 * @create: 2020-10-13 13:31:38
 */
@Slf4j
@Component
public class VisitViewTimerController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    VisitViewService visitViewService;

    @Autowired
    UserInfoService userInfoService;

    @Scheduled(cron = "0 25 0 * * ?") // 每天0点25执行一次
    public void sendMessageToIMTimer() throws Exception {
        log.info("开始随机新增用户访问量...");
        List list = userInfoService.findCondition();
        int count = 0;
        Random random = new Random();
        int r = 0;//随机数
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                UserInfo userInfo = (UserInfo) list.get(i);
                if (userInfo != null) {
                    VisitView v = null;
                    v = visitViewService.findVisitView(userInfo.getUserId());
                    if (v == null) {
                        //无访问量时 随机1-10
                        r = random.nextInt(10) + 1;
                        v = new VisitView();
                        v.setTotalVisitCount(r);
                        v.setTodayVisitCount(r);
                        v.setMyId(CommonUtils.getMyId());
                        v.setUserId(userInfo.getUserId());
                        count = visitViewService.add(v);
                    } else {
                        //已有访问量时 判断目前访问量处于什么级别 并根据级别进行新增
                        if (v.getTotalVisitCount() <= 15) {
                            r = random.nextInt(10) + 5;
                        } else if (v.getTotalVisitCount() > 15 && v.getTotalVisitCount() <= 100) {
                            r = random.nextInt(10) + 1;
                        } else if (v.getTotalVisitCount() > 100 && v.getTotalVisitCount() <= 150) {
                            r = random.nextInt(10) + 10;
                        } else if (v.getTotalVisitCount() > 150 && v.getTotalVisitCount() <= 200) {
                            r = random.nextInt(10) + 1;
                        } else if (v.getTotalVisitCount() > 200 && v.getTotalVisitCount() <= 300) {
                            r = random.nextInt(10) + 10;
                        } else if (v.getTotalVisitCount() > 300 && v.getTotalVisitCount() <= 400) {//大概一个月时200-400左右
                            r = random.nextInt(10) + 10;
                        } else if (v.getTotalVisitCount() > 400 && v.getTotalVisitCount() <= 500) {
                            r = random.nextInt(10) + 1;
                        } else if (v.getTotalVisitCount() > 500 && v.getTotalVisitCount() <= 800) {
                            r = random.nextInt(10) + 10;
                        } else if (v.getTotalVisitCount() > 800 && v.getTotalVisitCount() <= 1000) {//大概两个月时800左右
                            r = random.nextInt(30) + 20;
                        } else if (v.getTotalVisitCount() > 1000 && v.getTotalVisitCount() <= 5000) {//超过1000后平均每天新增30-80个
                            r = random.nextInt(50) + 30;
                        } else {//超过5000后平均每天新增1-30个
                            r = random.nextInt(30) + 1;
                        }
                        v.setTodayVisitCount(v.getTodayVisitCount() + r);
                        v.setTotalVisitCount(v.getTotalVisitCount() + r);
                        count = visitViewService.update(v);
                    }
                    //更新缓存
                    redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT + userInfo.getUserId(), CommonUtils.objectToMap(v), CommonUtils.getCurrentTimeTo_12());//今日访问量的生命周期 到今天晚上12点失效
                    if (count <= 0) {
                        log.info("定时任务新增用户[" + userInfo.getUserId() + "]今日访问量和总访问量到数据库失败");
                    } else {
                        log.info("定时任务新增用户[" + userInfo.getUserId() + "]今日访问量和总访问量[" + r + "]到数据库成功");
                    }
                }
            }
        }
    }
}
