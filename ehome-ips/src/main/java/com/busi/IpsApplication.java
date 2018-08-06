package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 公告栏系统（需求汇系统）主入口
 * author：SunTianJie
 * create time：2018/7/23 10:15
 */
@SpringBootApplication
@EnableEurekaClient
public class IpsApplication {
    public static void main(String[] args) {

        SpringApplication.run(IpsApplication.class,args);
    }

}
