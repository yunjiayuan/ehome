package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 云家园 活动相关应用 如：喂鸟 红包雨 赢大奖 校花活动 抢红包（分享得红包）等
 * author：SunTianJie
 * create time：2018/8/6 10:24
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivityApplication.class,args);
    }
}
