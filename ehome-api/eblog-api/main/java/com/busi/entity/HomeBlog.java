package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;
import java.util.Date;

/***
 * 生活圈实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class HomeBlog {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;//发布者用户ID

//    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!#￥$%*()\\-\\s*]", message = "标题格式有误,标题最长为30字，并且不能包含非法字符")
    @Length(max = 30, message = "标题内容不能超过30字")
    private String title;//标题（最多30个字）

//    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,，\\.。;；\\:\"'？?！!#@￥$%*（）《》()&|、‘’”“<>\\-\\s*]", message = "内容格式有误,不能包含非法字符")
    @Length(max = 10000, message = "内容不能超过1万字")
    private String content;//内容（最多1万字）

    @Length(max = 140, message = "基本内容字数最多140字")
    private String contentTxt;//基本内容（最多140字）

    @Length(max = 500, message = "图片地址格式有误")
    private String imgUrl;//图片地址组合，逗号分隔，最多九张图片

    @Length(max = 100, message = "视频地址格式有误")
    private String videoUrl;//视频地址

    @Length(max = 100, message = "视频封面地址格式有误")
    private String videoCoverUrl;//视频封面地址

    @Length(max = 100, message = "语音地址格式有误")
    private String audioUrl;//语音地址

    private long musicId;//音乐主键Id

    private String songName;//背景音乐的歌名

    private String singer; //背景音乐的歌手

    @Max(value = 3, message = "sendType参数有误，超出指定范围")
    @Min(value = 0, message = "sendType参数有误，超出指定范围")
    private int sendType;//发布博文类型：0纯文 1图片 2视频 3音频

    @Max(value = 3, message = "classify参数有误，超出指定范围")
    @Min(value = 0, message = "classify参数有误，超出指定范围")
    private int classify;//查看权限：0公开博文 1私密博文 2给谁看 3不给谁看

    @Length(max = 3000, message = "classifyUserIds参数有误，超出指定范围")
    private String classifyUserIds;//允许或者不允许查看的用户ID组合，逗号分隔， 当classify=2或者classify=3时有效

    @Max(value = 43, message = "tag参数有误，超出指定范围")
    @Min(value = 0, message = "tag参数有误，超出指定范围")
    private int tag;//标签类型 0吐槽1接棒2名人3明星4企业5单位6政府7官员8店主9学生10媒体11附近12草根13热点14文化15财经16情感17健康18旅游19教育20美食21体育22时尚23汽车24杂谈25IT26星座27校园28房产29其他30分享31求助

    private long shareCount;//转载次数

    private long likeCount;//喜欢次数（点赞次数）

    private long lookCount;//浏览次数

    private long commentCount;//评论次数

    @Max(value = 3, message = "blogType参数有误，超出指定范围")
    @Min(value = 0, message = "blogType参数有误，超出指定范围")
    private int blogType;//博文类型：0原创普通博文 1转载普通博文 2求助悬赏博文 3出售点子博文

    @Min(value = 0, message = "shareBlogId参数有误，超出指定范围")
    private long shareBlogId;//被转发的博文ID

    @Min(value = 0, message = "shareUserId参数有误，超出指定范围")
    private long shareUserId;//被转发的用户ID

    @Min(value = 0, message = "origBlogId参数有误，超出指定范围")
    private long origBlogId;//最原始博文的ID

    @Min(value = 0, message = "origUserId参数有误，超出指定范围")
    private long origUserId;//最原始博文的用户ID

//    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,，\\.。;；\\:\"'？?！!#@￥$%*（）《》()&|、‘’”“<>\\-\\s*]", message = "内容格式有误,不能包含非法字符")
    @Length(max = 140, message = "转发评论内容不能超过140")
    private String reprintContent;//转发时的评论内容（最多140字）

    private long accessId;//查看权限ID（对应权限分组表主键ID）

    @Max(value = 2, message = "blogStatus参数有误，超出指定范围")
    @Min(value = 0, message = "blogStatus参数有误，超出指定范围")
    private int blogStatus;//博文状态  0正常 1删除 2锁定

    @Digits(integer = 3, fraction = 6, message = "longitude参数格式有误")
    private double longitude;//东经

    @Digits(integer = 3, fraction = 6, message = "latitude参数格式有误")
    private double latitude;//北纬

    @Length(max = 100, message = "位置信息字数太长了")
    private String position;//具体位置信息

    private int cityId;//百度地图中的城市ID，用于同城搜索

    @Max(value = 1, message = "anonymousType参数有误，超出指定范围")
    @Min(value = 0, message = "anonymousType参数有误，超出指定范围")
    private int anonymousType;//是否匿名发布 0表示正常发布不匿名  1表示匿名发布  别人能看到博文  无法查看名字和门牌号 也不能通过家博进行访问串门

    private String shareInfo;//分享信息记录，格式：1,123,2;2,123;  分号为一组，逗号分隔第一位代表分享类型： 1.发布公告 2.发布家博，如果是公告，逗号分隔后会有三个元素依次为：公告、公告ID、公告分类ID；家博的话数组为俩个元素 依次为：家博、家博ID

    private double reward;//求助类型的博文中的出售金额或悬赏金额

    @Min(value = 0, message = "firstPayUserId参数有误，超出指定范围")
    private long firstPayUserId;//点子第一个购买用户

    @Max(value = 1, message = "solve参数有误，超出指定范围")
    @Min(value = 0, message = "solve参数有误，超出指定范围")
    private int solve;//求助类:0未解决 1已解决;点子类0未成交1成交

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//发布时间

    private int remunerationStatus; //稿费作品状态 0不是稿费作品 1是一级稿费作品 2是二级稿费作品 3是三级稿费作品 4是四级稿费作品
    private double remunerationMoney; //稿费奖励金额
    private long remunerationUserId;//审核人用户ID
    private Date remunerationTime;//审核时间

    private int isLike;          //是否点赞  0未点赞 1点赞  与数据库无关字段
    private String userName;      //发布者用户名	 与数据库无关字段
    private String userHead;    //发布者头像	 与数据库无关字段
    private int proTypeId;      //发布者省简称ID 与数据库无关字段
    private long houseNumber;   //发布者门牌号   与数据库无关字段
    private double distance;    //距离	 与数据库无关字段
//  private String shareName;	  //shareUserId 用户名称
//  private String origUserName;//原始博主用户名 转载使用

}
