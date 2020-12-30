package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/***
 * 足迹实体类
 * author：ZhaoJiaJie
 * create time：2018-9-29 14:26:03
 */
@Setter
@Getter
public class Footmark {

    private long id;        //主键

    private String title;    //标题

    private long userId;        //登入用户

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加足迹时间

    private String imgUrl; // 图片路径

    private String videoUrl;    //视频路径     1个

    private String audioUrl;    //音频路径     1个

    private String infoId;        //信息id 公告ID和分类ID(用,分隔，格式：123,4):1婚恋交友,2二手手机,3寻人,4寻物,5失物招领,6其他（注：后续添加）

    private int footmarkStatus; //足迹状态  0正常  1删除

    private int footmarkType;    //足迹类型 0.默认全部 1.发布公告 2.发布家博 3.图片上传 4.音频上传 5.视频上传  6记事  7日程

    //与数据库无关字段
    private String users;        //用户ID+名字+头像：逗号分隔；多个之间分号分隔
}
