package com.busi.Feign;


import com.busi.controller.local.HomeBlogMessageLocalController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * fegin调用注意事项
 * 1、继承将要调用的接口，不用重写父类接口方法，直接使用
 * 2、子类需要添加@FeignClient("父类项目对外调用访问的名称")
 * 3、启动类需要添加@EnableFeignClients
 * 4、调用方法时，一般参数最多一个，可以传对象
 * 5、调用时必须保证参数一致
 * author：SunTianJie
 * create time：2018/6/12 19:12
 */
@FeignClient("eblog")
@Component
public interface HomeBlogMessageLocalControllerFegin extends HomeBlogMessageLocalController {

}
