package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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

    private long userId;            //评论用户ID

    private long replayId;            //被回复用户ID  我回复@XXX:

    private long masterId;            //博主用户ID

    private String content;            //评论的内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //评论的时间

    private int replyType;             //0评论 1回复 2转发评论

    private int replyStatus;            //0正常 1删除

    private long fatherId;              //评论根父级ID

    private long secondFatherId;         //评论次父级ID

    //与数据库无关字段
    private String replayName;    //被回复用户名称
    private String userName;        //评论用户 名称
    private String userHead;        //评论用户 头像
    private int proTypeId;          //评论用户 省简称ID
    private long houseNumber;       //评论用户 门牌号
    private long replyNumber;       //回复数
    private List messageList;      //回复集合


}
