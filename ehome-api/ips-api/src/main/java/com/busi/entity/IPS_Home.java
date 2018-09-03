package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @program: 公告Home类
 * @author: ZHaoJiaJie
 * @create: 2018-08-09 14:10
 */
@Setter
@Getter
public class IPS_Home {

    private long id;                    //主键

    private long userId;				//用户

    private String title;				//标题

    private String content;				//内容简介

    private long infoId;				//公告id

    private int afficheType;			//公告类别标志：1婚恋交友,2二手手机,3寻人,4寻物,5失物招领,6其他 7发简历找工作 8发布招聘（注：后续添加）

    private long seeNumber;				//浏览数

    private long likeNumber;			//喜欢数

    private int auditType;				//审核标志：1审核中，2通过，3未通过

    private int deleteType;				//删除标志：1未删除，2用户删除，3管理人员删除

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date refreshTime;			//刷新时间（注：未刷新之前为发布时间）

    private int frontPlaceType;			//是否置顶：0未置顶  1当前分类置顶  2推荐列表置顶

    private int levelType;				//级别

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date releaseTime;			//发布时间

    private String mediumImgUrl;		//图片地址

    private int sellType;				//商品买卖状态 : 1已上架，2已下架，3已卖出

    private int fraction;//公告分数

}

