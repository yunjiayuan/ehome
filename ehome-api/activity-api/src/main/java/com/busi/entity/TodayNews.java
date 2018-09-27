package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 资讯
 * @author: ZHaoJiaJie
 * @create: 2018-9-27 10:31:25
 */
@Setter
@Getter
public class TodayNews {

    private long id;                          //主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                      //用户ID

    private String title;                     //标题

    private String content;                   //内容

    private String imgUrls;                   //图片

    private String videoUrl;                  //视频地址     1个

    private String coverUrl;                  //视频封面图

    private int newsType;                     //新闻类别   0今日人物  1今日企业  2今日新闻

    private int newsFormat;                   //发布类型  0纯文  1一图  2多图  3视频

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;                     //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;                 //刷新时间

    private long commentCount;                //评论数

    private int newsState;                    //新闻状态 0正常 1删除

}
