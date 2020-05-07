package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 黑店定时任务系统主入口
 * author：ZJJ
 * create time：2020-5-7 12:04:07
 */
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class QuartzShopFloorApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzShopFloorApplication.class,args);
    }

}
