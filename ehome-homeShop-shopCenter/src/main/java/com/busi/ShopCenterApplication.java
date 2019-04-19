package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 家店系统-家店中心 启动类
 * author：SunTianJie
 * create time：2019/4/17 15:24
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ShopCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopCenterApplication.class,args);
    }
}
