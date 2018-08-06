package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 获取家主页信息接口
 * author：SunTianJie
 * create time：2018/7/16 13:48
 */
public interface HomePageInfoApiController {

    /***
     * 获取指定用户ID的家主页信息
     * @param userId
     * @return
     */
    @GetMapping("findHomePageInfo/{userId}")
    ReturnData findHomePageInfo(@PathVariable long userId);
}
