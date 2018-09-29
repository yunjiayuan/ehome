package com.busi.controller.local;

import com.busi.entity.Footmark;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 * 足迹相关接口(服务期间调用)
 * author：zhaojiajie
 * create time：2018-9-29 16:20:34
 */
public interface FootmarkLocalController {

    /***
     * 新增
     * @param footmark
     * @return
     */
    @PostMapping("addFootmark")
    ReturnData addFootmark(@RequestBody Footmark footmark);

}
