package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 生活圈浏览相关接口
 * author：zhaojiajie
 * create time：2018-11-8 16:59:07
 */
public interface HomeBlogLookApiController {

    /***
     * 更新浏览量接口
     * @param userId
     * @return
     */
    @GetMapping("updateLook/{userId}/{blogId}")
    ReturnData updateLook(@PathVariable long userId, @PathVariable long blogId);

}
