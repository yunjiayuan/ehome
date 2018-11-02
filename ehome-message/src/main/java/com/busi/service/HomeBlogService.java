package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.HomeBlogLocalControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.HomeBlog;
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
public class HomeBlogService implements MessageAdapter {

    @Autowired
    private HomeBlogLocalControllerFegin homeBlogLocalControllerFegin;

    /***
     * fegin 调用eblog服务中的 更新生活圈评论数、点赞数、浏览量、转发量
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            long blogId = Long.parseLong(body.getString("blogId"));
            int count = Integer.parseInt(body.getString("count"));
            int type = Integer.parseInt(body.getString("type"));
            HomeBlog homeBlog = new HomeBlog();
            homeBlog.setUserId(userId);
            homeBlog.setId(blogId);
            if(type==0){//点赞
                homeBlog.setLikeCount(count);
            }else if(type==1){//评论
                homeBlog.setCommentCount(count);
            }else if(type==2){//转发
                homeBlog.setShareCount(count);
            }else if(type==3){//浏览
                homeBlog.setLookCount(count);
            }else{
                log.info("消息服务平台处理用户更新生活圈评论数、点赞数、浏览量、转发量操作失败，参数有误："+body.toJSONString());
                return;
            }
            homeBlogLocalControllerFegin.updateBlog(homeBlog);//fegin调用更新操作
            log.info("消息服务平台处理用户更新生活圈评论数、点赞数、浏览量、转发量操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户更新生活圈评论数、点赞数、浏览量、转发量操作失败，参数有误："+body.toJSONString());
        }
    }
}
