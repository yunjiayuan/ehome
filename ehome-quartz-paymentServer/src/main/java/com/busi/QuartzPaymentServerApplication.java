package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 支付相关 定时任务系统 如：红包过期退款
 * author：SunTianJie
 * create time：2019/1/17 17:20
 */
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@EnableFeignClients
public class QuartzPaymentServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzPaymentServerApplication.class,args);
    }
}
