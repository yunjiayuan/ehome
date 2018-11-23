package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.HomeBlogCommentLocalControllerFegin;
import com.busi.Feign.HomeBlogLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.HomeBlog;
import com.busi.entity.HomeBlogComment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新生活圈评论数、点赞数、浏览量、转发量service
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class HomeBlogCommentService implements MessageAdapter {

    @Autowired
    private HomeBlogCommentLocalControllerFegin homeBlogCommentLocalControllerFegin;

    /***
     * fegin 调用eblog服务中的 更新生活圈评论的回复数
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long commentId = Long.parseLong(body.getString("commentId"));
            int count = Integer.parseInt(body.getString("count"));
            HomeBlogComment homeBlogComment = new HomeBlogComment();
            homeBlogComment.setId(commentId);
            homeBlogComment.setReplyNumber(count);
            homeBlogCommentLocalControllerFegin.updateCommentNum(homeBlogComment);//fegin调用更新操作
            log.info("消息服务平台处理用户更新生活圈评论的回复数操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户更新生活圈评论的回复数操作失败，参数有误："+body.toJSONString());
        }
    }
}
