package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户（login）相关定时任务系统主入口 如删除图片等
 * author：SunTianJie
 * create time：2018/7/23 10:15
 */
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class QuartzLoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzLoginApplication.class,args);
    }

}
