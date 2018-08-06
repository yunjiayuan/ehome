package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 用户相关业务入口 例如注册 登录 修改用户信息等
 * author：SunTianJie
 * create time：2018/6/4 17:23
 */
@SpringBootApplication
@EnableEurekaClient
public class LoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class,args);
    }
}
