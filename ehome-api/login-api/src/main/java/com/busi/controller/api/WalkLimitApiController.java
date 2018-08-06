package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 随便走走 各地串串接口
 * author：SunTianJie
 * create time：2018/7/25 18:59
 */
public interface WalkLimitApiController {
    /***
     * 获取随便走走 各地串串的 目标用户ID
     * @return
     */
    @GetMapping("walk")
    ReturnData walk();
}
