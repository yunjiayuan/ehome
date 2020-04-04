package com.busi.timerController;

import com.busi.entity.GrabGifts;
import com.busi.entity.GrabMedium;
import com.busi.servive.GrabGiftsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * quartz定时器:抢礼物假数据
 * author：ZHaoJiaJie
 * create time：2020-04-04 22:43:52
 */
@Slf4j
@Component
public class GrabGiftsTimerController {

    @Autowired
    GrabGiftsService grabGiftsService;

    @Scheduled(cron = "0 * */1 * * ?") //每1小时一次
    public void wheelPlantingTimer() throws Exception {
        log.info("开始执行抢礼物定时任务...");
        //查询奖品
        GrabGifts grabGifts = grabGiftsService.findGifts();
        if (grabGifts == null) {
            log.info("奖品不存在...");
            return;
        }
        //判断当前时间是否是0点
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒
        long time = calendar.getTimeInMillis(); // 此处为今天的00：00：00
        long da = new Date().getTime();//当前时间毫秒数
        long curren = 300000;//五分钟毫秒数
        long curren2 = 3600000;//一小时毫秒数
        if (da <= time + curren && da >= time) {
            //初始化奖品数量
            grabGifts.setNumber(20);
            grabGiftsService.update(grabGifts);
            log.info("初始化奖品数量成功...");
            return;
        }
        //更新数据库奖品数量，同时新增一名中奖人员记录
        if (grabGifts.getNumber() == 0) {
            log.info("奖品数量为0...");
            return;
        }
        if (da >= time + curren2 * 4) {
            grabGifts.setNumber(grabGifts.getNumber() - 1);
            grabGiftsService.update(grabGifts);
            GrabMedium medium = new GrabMedium();
            long timeCount = (long) (Math.random() * 2999999) + 1;//50分钟内随机
            long time2 = da + timeCount;
            long newUserId = (long) (Math.random() * 40000) + 13870;
            medium.setUserId(newUserId);
            medium.setPrice(12699);
            medium.setCost("Apple iPhone 11 Pro Max");
            medium.setTime(new Date(time2));
            medium.setWinningState(1);
            grabGiftsService.add(medium);
            log.info("抢礼物定时任务执行完成...");
        }
    }
}
