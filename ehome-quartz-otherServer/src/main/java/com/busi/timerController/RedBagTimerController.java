package com.busi.timerController;

import com.busi.entity.RedBagRain;
import com.busi.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: ehome
 * @description: 红包雨定时任务
 * @author: ZHaoJiaJie
 * @create: 2019-01-16 17:27
 */
@Slf4j
@Component
public class RedBagTimerController {

    @Autowired
    TaskService taskService;

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

//    @Scheduled(cron = "0 0/1 * * * ?") // 每天12:30、19、22、24点开始
    @Scheduled(cron = "0 30 12,19,22,0 * * ?") // 每天12:30、19、22、24点开始
    public void redBagTimer() throws Exception {
        //删除过期数据
        taskService.batchDel();
        log.info("删除红包雨过期数据成功！！！");
        try {
            //随机10个中奖人员
            int count = 0;
            long timeCount = (long) (Math.random() * 420000) + 182134;
            long time = new Date().getTime() + timeCount;
            long newUserId = (long) (Math.random() * 40001) + 13870;

            //添加一等奖一名
            RedBagRain robotUser = new RedBagRain();
            robotUser.setUserId(newUserId);
            robotUser.setTime(new Date(time));
            robotUser.setQuota(666.00);
            robotUser.setPizeType(1);
            taskService.addRain(robotUser);

            //添加二等奖九名
            count = 9;
            for (int i = 0; i < count; i++) {
                newUserId = (long) (Math.random() * 40001) + 13870;
                timeCount = (long) (Math.random() * 420000) + 182134;
                time = new Date().getTime() + timeCount;
                double sum = (Math.random() * 391) + 200;
                RedBagRain robotUser2 = new RedBagRain();
                if (newUserId != robotUser.getUserId()) {
                    robotUser2.setUserId(newUserId);
                }
                robotUser2.setTime(new Date(time));
                robotUser2.setQuota(sum);
                robotUser2.setPizeType(1);
                taskService.addRain(robotUser2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("添加红包雨中奖人员名单成功！！！");
    }
}
