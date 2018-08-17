package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 统一支付平台 支付相关服务 如充值 钱包 钱包付款 发红包 收红包等等
 * author：SunTianJie
 * create time：2018/8/6 10:19
 */
@SpringBootApplication
@EnableEurekaClient
public class PaymentServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServerApplication.class,args);
    }
}
