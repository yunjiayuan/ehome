package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 公告栏系统（需求汇系统）主入口
 * author：SunTianJie
 * create time：2018/7/23 10:15
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class IpsApplication {
    public static void main(String[] args) {

        SpringApplication.run(IpsApplication.class,args);
    }

}
