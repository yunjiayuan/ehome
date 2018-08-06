package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/6/5 14:57
 */
@RestController
public class TestController extends BaseController {

    @Autowired
    RedisUtils redisUtils;

    @PostMapping("/login/testAdd/{name}")
    public ReturnData add(@PathVariable("name") String name){
//        String houseNumber = "1000000";
//        if(redisUtils.isExistKey("houseNumber")){
//            houseNumber = (String)redisUtils.getKey("houseNumber");
//        }else{
//            redisUtils.set("houseNumber",houseNumber,0);
//        }
//        int count  = Integer.parseInt(houseNumber);
//        count = count+1;
//        redisUtils.set("houseNumber","1000000",0);
//        System.out.println(redisUtils.incr("houseNumber",1));;
        String [] array = {"houseNumber","name","password"};
        List<Object> list = redisUtils.multiGet("user_17",array);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,name,null);
    }

}
