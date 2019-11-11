package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * HTTPS请求（主要提供需要https的接口）主入口
 * author：SunTianJie
 * create time：2018/7/23 10:12
 */
@SpringBootApplication
@EnableEurekaClient
public class HttpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HttpsApplication.class,args);
    }
}
