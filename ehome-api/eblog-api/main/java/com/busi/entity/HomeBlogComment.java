package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/***
 * 生活圈评论实体类
 * author：ZHaoJiaJie
 * create time：2018-11-5 13:47:54
 */
@Setter
@Getter
public class HomeBlogComment {

    private long id;                //主建ID

    private long blogId;            //博文ID

    private long userId;            //评论用户ID    -评论用户

    private long replayId;            //回复评论情况 -被回复用户ID  我回复@XXX:

    private long masterId;            //博主用户ID

    private String content;            //评论的内容

    private Date time;                //评论的时间

    private int replyType;                //0评论 1回复

    private int replyStatus;                //0正常 1删除

    //与数据库无关字段
    private String replayname;    //回复评论用户名称
    private String username;        //评论用户 名称
    private String userHead;        //评论用户 头像


}
