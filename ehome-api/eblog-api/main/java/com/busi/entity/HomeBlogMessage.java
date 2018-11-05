package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/***
 * 生活圈评论转发 @ 消息实体类
 * author：ZHaoJiaJie
 * create time：2018-11-5 13:47:54
 */
@Setter
@Getter
public class HomeBlogMessage {

    private long id;                //主建ID

    private long userId;            //发出消息用户

    private long replayId;            //接收消息用户

    private long masterId;            //博主用户ID

    private long commentId;            //消息ID

    private String content;            //消息内容

    private Date time;                //消息时间

    private int newsType;                //消息类型 0评论 1回复 2赞 3转发 4评论@  5回复@  6转发@ 7博文@

    private int newsState;                //消息状态 0已读 1未读

    private int status;                //0正常 1删除

    //与数据库无关字段
//    private HomeBlog blog;            //博文ID
//    private HomeBlogComment parentMessage;    //消息回复父级ID

}
