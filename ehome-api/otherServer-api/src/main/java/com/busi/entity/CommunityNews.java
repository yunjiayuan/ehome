package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 资讯
 * @author: ZHaoJiaJie
 * @create: 2020-03-20 11:37:59
 */
@Setter
@Getter
public class CommunityNews {
    private long id;                          //主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                      //用户ID

    @Min(value = 1, message = "communityId参数有误")
    private long communityId;    //newsType=0时居委会ID  newsType=1时物业ID

    @Length(max = 100, message = "标题最多可输入100个字")
    private String title;                     //标题

    @Length(max = 10000, message = "内容最多可输入10000个字")
    private String content;                   //内容

    private String imgUrls;                   //图片

    private String videoUrl;                  //视频地址     1个

    private String coverUrl;                  //视频封面图

    private int newsType;                     //资讯类别   0居委会  1物业   2点对点通知通告（普通居民） 3内部人员通知

    private int newsFormat;                   //发布类型  0纯文  1一图  2多图  3视频

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;                     //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;                 //刷新时间

    private long commentCount;                //评论数

    private int newsState;                    //资讯状态 0正常 1删除

    private String identity;            //可查看通告身份  只在newsType=2时有效
}
