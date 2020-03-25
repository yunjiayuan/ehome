package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;

/***
 * 居委会、物业消息实体类
 * author：ZHaoJiaJie
 * create time：2020-03-24 12:58:51
 */
@Setter
@Getter
public class CommunityMessage {

    private long id;                //主建ID

    private long userId;            //发出消息用户

    private long replayId;            //收到消息用户

    private long communityId;    //type=0时为居委会ID  type=1时为物业ID

    private int type;         //类别   0居委会  1物业

    private long commentId;            //评论ID

    private String content;            //消息内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //消息时间

    private int newsType;                //消息类型 0评论 1回复

    private int newsState;                //消息状态 0已读 1未读

    private int status;                //0正常 1删除

    //与数据库无关字段
    private String replayName;      //被回复用户名称

    private String userName;        //评论用户 名称

    private String userHead;        //评论用户 头像

    private String name;            // 居委会或物业名称

    private String cover;           // 封面

}
