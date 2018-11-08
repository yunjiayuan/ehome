package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HomeBlogMessage;
import com.busi.entity.ReturnData;
import com.busi.service.HomeBlogCommentService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @program: ehome
 * @description: 生活圈新增消息
 * @author: ZHaoJiaJie
 * @create: 2018-11-08 13:10
 */
@RestController
public class HomeBlogMessageLController extends BaseController implements HomeBlogMessageLocalController {

    @Autowired
    private HomeBlogCommentService homeBlogCommentService;

    /***
     * 新增消息
     * @param homeBlogMessage
     * @return
     */
    @Override
    public ReturnData addMessage(@RequestBody HomeBlogMessage homeBlogMessage) {

        homeBlogMessage.setNewsState(1);
        homeBlogMessage.setTime(new Date());
        homeBlogCommentService.addMessage(homeBlogMessage);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

}
