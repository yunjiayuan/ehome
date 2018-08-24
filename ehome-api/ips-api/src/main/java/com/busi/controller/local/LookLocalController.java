package com.busi.controller.local;

import com.busi.entity.Look;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 * look
 * author：zhaojiajie
 * create time：2018-8-24 15:26:44
 */
public interface LookLocalController {

    /***
     * 新增
     * @param look
     * @return
     */
    @PostMapping("addLook")
    ReturnData addLook(@RequestBody Look look);

}
