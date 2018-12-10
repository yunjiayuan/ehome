package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.HomeBlogMessageLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.HomeBlogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 新增生活圈消息service
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class HomeBlogMessageService implements MessageAdapter {

    @Autowired
    private HomeBlogMessageLocalControllerFegin homeBlogMessageLocalControllerFegin;

    /***
     * fegin 调用eblog服务中的 新增生活圈消息
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            long replayId = Long.parseLong(body.getString("replayId"));
            long masterId = Long.parseLong(body.getString("masterId"));
            long blog = Long.parseLong(body.getString("blog"));
            long commentId = Integer.parseInt(body.getString("commentId"));
            int newsType = Integer.parseInt(body.getString("newsType"));
            String content = body.getString("content");
            HomeBlogMessage homeBlogMessage = new HomeBlogMessage();
            homeBlogMessage.setUserId(userId);
            homeBlogMessage.setReplayId(replayId);
            homeBlogMessage.setMasterId(masterId);
            homeBlogMessage.setBlog(blog);
            homeBlogMessage.setCommentId(commentId);
            homeBlogMessage.setNewsType(newsType);
            homeBlogMessage.setContent(content);
            homeBlogMessageLocalControllerFegin.addMessage(homeBlogMessage);//fegin调用更新操作
            log.info("消息服务平台处理用户新增生活圈消息操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户新增生活圈消息操作失败，参数有误："+body.toJSONString());
        }
    }
}
