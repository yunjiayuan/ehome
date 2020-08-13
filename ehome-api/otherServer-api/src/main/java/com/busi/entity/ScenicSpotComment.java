package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/***
 * 景区评论实体类
 * author：ZHaoJiaJie
 * create time：2020-08-12 13:06:30
 */
@Setter
@Getter
public class ScenicSpotComment {

    private long id;                //主建ID

    private long masterId;            //景区ID

    private long userId;            //评论者ID

    private long replayId;            //被评论(回复)用户ID

    private String content;            //评论的内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //评论的时间

    private int replyType;             //0评论 1回复

    private int replyStatus;            //0正常 1删除

    private long fatherId;              //评论根父级ID

    private long secondFatherId;         //评论次父级ID

    private long replyNumber;       //回复数

    private long orderId;            //	订单Id

    private String imgUrls;                //图片

    private int score;                        // 评分：1一星 2二星 3三星 4 四星 5 五星

    private int anonymousType;  //是否匿名发布 0表示正常发布不匿名  1表示匿名发布  别人能看到评论  无法查看名字和门牌号

    //与数据库无关字段
    private String replayName;    //被回复用户名称
    private String userName;        //评论用户 名称
    private String userHead;        //评论用户 头像
    private int proTypeId;          //评论用户 省简称ID
    private long houseNumber;       //评论用户 门牌号
    private List messageList;      //回复集合
}
