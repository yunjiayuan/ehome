package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 其他应用服务定时任务系统主入口，如 喂鸟等
 * author：SunTianJie
 * create time：2018/7/23 10:15
 */
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class QuartzOtherServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzOtherServerApplication.class,args);
    }

}
