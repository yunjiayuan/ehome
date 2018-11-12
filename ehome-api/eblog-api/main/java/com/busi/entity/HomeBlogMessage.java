package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/***
 * 生活圈消息实体类
 * author：ZHaoJiaJie
 * create time：2018-11-5 13:47:54
 */
@Setter
@Getter
public class HomeBlogMessage {

    private long id;                //主建ID

    private long userId;            //发出消息用户

    private long replayId;            //接收消息用户

    private long blog;                  //博文ID

    private long commentId;            //评论ID

    private String content;            //消息内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //消息时间

    private int newsType;                //消息类型 0评论 1回复 2赞 3转发

    private int newsState;                //消息状态 0已读 1未读

    private int status;                //0正常 1删除


    private String userName;  		//评论用户 名称	-查询后从内存获取最新  与数据库无关字段
    private String userHead;  		//评论用户 头像	-查询后从内存获取最新  与数据库无关字段

}
