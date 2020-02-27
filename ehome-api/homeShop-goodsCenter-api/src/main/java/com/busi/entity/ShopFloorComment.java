package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
/***
 * 楼店评论实体类
 * author：ZHaoJiaJie
 * create time：2020-02-24 11:35:48
 */
@Setter
@Getter
public class ShopFloorComment {

    private long id;                //主建ID

    private long goodsId;            //商品ID

    private long userId;            //评论者ID

    private long replayId;            //被评论(回复)用户ID

    private String content;            //评论的内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //评论的时间

    private int replyType;             //0评论 1回复

    private int replyStatus;            //0正常 1删除

    private long fatherId;              //评论根父级ID

    private long replyNumber;       //回复数

    //与数据库无关字段
    private String replayName;    //被回复用户名称
    private String userName;        //评论用户 名称
    private String userHead;        //评论用户 头像
    private int proTypeId;          //评论用户 省简称ID
    private long houseNumber;       //评论用户 门牌号
    private List messageList;      //回复集合
}
