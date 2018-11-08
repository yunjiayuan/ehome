package com.busi.controller.local;

import com.busi.entity.HomeBlogMessage;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 新增生活圈消息接口
 * author：ZHaoJiaJie
 * create time：22018-11-8 10:27:52
 */
public interface HomeBlogMessageLocalController {

    /***
     * 新增消息
     * @param homeBlogMessage
     * @return
     */
    @PostMapping("addMessage")
    ReturnData addMessage(@RequestBody HomeBlogMessage homeBlogMessage);
}
