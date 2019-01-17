package com.busi.timerController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * quartz定时器:二手
 * author：SunTianJie
 * create time：2019/1/11 14:04
 */
@Slf4j
@Component
public class TestTimerController {

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
    @Scheduled(cron = "0/5 * * * * ?") // 启动执行
    public void usedDealTimer() throws Exception {
        log.info("启动测试定时任务...");

    }


//    @Scheduled(fixedRate = 5000)//每5秒执行一次
//    public void play() throws Exception {
//        System.out.println("执行每5秒钟调度任务："+new Date());
//    }
//
//
//    @Scheduled(cron = "0/2 * * * * ?") //每2秒执行一次
//    public void doSomething() throws Exception {
//        System.out.println("执行每2秒钟调度任务："+new Date());
//    }
//
//    @Scheduled(cron = "0 0 0/1 * * ? ") // 每一小时执行一次
//    public void goWork() throws Exception {
//        System.out.println("执行每1小时调度任务："+new Date());
//    }

}
