package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 生活圈（家博系统）主入口
 * author：SunTianJie
 * create time：2018/7/23 10:12
 */
@SpringBootApplication
@EnableEurekaClient
public class EblogApplication {
    public static void main(String[] args) {
        SpringApplication.run(EblogApplication.class,args);
    }
}
