package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 云家园 其他应用服务
 * author：SunTianJie
 * create time：2018/8/6 10:19
 */
@SpringBootApplication
@EnableEurekaClient
public class OtherServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtherServerApplication.class,args);
    }
}
