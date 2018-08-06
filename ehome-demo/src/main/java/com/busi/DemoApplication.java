package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/5/29 14:42
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
//@MapperScan(basePackages = { "com.busi.dao" }, sqlSessionFactoryRef = "sqlSessionFactory")
public class DemoApplication {
    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class,args);
    }
}
