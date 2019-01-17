package com.busi.timerController;

import com.busi.entity.BirdFeedingData;
import com.busi.service.BirdJournalService;
import com.busi.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * quartz定时器:喂鸟
 * author：ZHJJ
 * create time：22019-1-16 11:46:11
 */
@Slf4j
@Component
public class BirdJournalTimerController {

    @Autowired
    BirdJournalService birdJournalService;

    /**
     * Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
     * <p>
     * “*”可用在所有字段中，表示对应时间域的每一个时刻，例如，*在分钟字段时，表示“每分钟”；
     * <p>
     * “?”字符：表示不确定的值 该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于点位符；
     * <p>
     * “,”字符：指定数个值 表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五；
     * <p>
     * “-”字符：指定一个值的范围 如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12；
     * <p>
     * “/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m
     * <p>
     * “L”字符：用在日表示一个月中的最后一天，用在周表示该月最后一个星期X
     * <p>
     * “W”字符：指定离给定日期最近的工作日(周一到周五)
     * <p>
     * “#”字符：表示该月第几个周X。6#3表示该月第3个周五
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 8,18 * * ?") // 每天早晨8点0分与下午6点0分开始
//    @Scheduled(cron = "0 0/2 * * * ?") // 每2分执行一次
    public void birdJournalTimer() throws Exception {
        int num = 0;
        int count = 5000;
        int userIdstart = 13870;
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int todaylastfeedbirddate = Integer.valueOf(format.format(new Date()));
        //删除过期数据
        birdJournalService.batchDel(Constants.birdCount, userIdstart, count);
        try {
            //差一次喂饱【喂九次】
            for (int i = 0; i < 2500; i++) {
                BirdFeedingData robotUser = new BirdFeedingData();
                robotUser.setEggState(0);//产蛋状态
                robotUser.setUserId(Constants.birdCount);//用户ID
                robotUser.setBirdBeFeedTotalCount(9);//今日自家鸟被喂次数
                robotUser.setBeenFeedBirdTotalCount(9); //被喂总次数
                robotUser.setBeenLastFeedBirdDate(todaylastfeedbirddate);//被喂时间
                birdJournalService.addData(robotUser);
                Constants.birdCount++;
            }
            // 产蛋中
            for (int i = 0; i < 2500; i++) {
                Date before4 = new Date(now.getTime() - 14400000);//开始时间（当前时间前4个小时【为防止马上成熟】）
                Date randomDate = randomDate(before4.getTime(), now.getTime());
                if (randomDate != null) {
                    BirdFeedingData robotUser = new BirdFeedingData();
                    robotUser.setEggState(1);//产蛋状态
                    robotUser.setUserId(Constants.birdCount);//用户ID
                    robotUser.setBirdBeFeedTotalCount(10);//今日自家鸟被喂次数
                    robotUser.setBeenFeedBirdTotalCount(10); //被喂总次数
                    robotUser.setStartLayingTime(randomDate);//随机产蛋时间
                    robotUser.setBeenLastFeedBirdDate(todaylastfeedbirddate);//被喂时间
                    birdJournalService.addData(robotUser);
                    Constants.birdCount++;
                }
            }
            if (Constants.birdCount == 53870) {
                num = 53870;
                Constants.birdCount = 13870;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num == 53870) {
            log.info("添加喂鹦鹉数据【" + (num - count) + "--" + (num - 1) + "】成功！！！");
        } else {
            log.info("添加喂鹦鹉数据【" + (Constants.birdCount - count) + "--" + (Constants.birdCount - 1) + "】成功！！！");
        }
    }

    /***
     * 生成随机时间
     * @param beginAnyTime
     * @param endAnyTime
     * @return
     */
    private static Date randomDate(long beginAnyTime, long endAnyTime) {
        try {
            if (beginAnyTime >= endAnyTime) {
                return null;
            }
            long date = random(beginAnyTime, endAnyTime);
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        //如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }
}
