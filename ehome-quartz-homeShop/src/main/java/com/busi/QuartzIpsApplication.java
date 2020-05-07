package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 需求汇（原公告栏）定时任务系统主入口
 * author：SunTianJie
 * create time：2018/7/23 10:15
 */
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class QuartzIpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzIpsApplication.class,args);
    }

}
