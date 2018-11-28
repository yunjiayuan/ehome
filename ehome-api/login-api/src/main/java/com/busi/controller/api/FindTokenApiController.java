package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 获取第三方token接口
 * author：SunTianJie
 * create time：2018/7/16 13:48
 */
public interface FindTokenApiController {

    /***
     * 获取七牛上传token
     * @return
     */
    @GetMapping("findToken")
    ReturnData findToken();
}
