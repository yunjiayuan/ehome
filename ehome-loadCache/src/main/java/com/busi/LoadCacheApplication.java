package com.busi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 加载数据库相关信息到缓存redis中
 * 加载 门牌号记录、账号预选记录
 * author：SunTianJie
 * create time：2018/7/3 14:45
 */
@SpringBootApplication
@EnableEurekaClient
public class LoadCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadCacheApplication.class,args);
    }
}
