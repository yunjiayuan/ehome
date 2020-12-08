package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 记事本实体类
 * @description:
 * @author: ZHaoJiaJie
 * @create: 2018-10-10 15:40:18
 */
@Setter
@Getter
public class Notepad {

    private long id;

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private int addType;            //类型ID 0日程1记事

    private String content;        //内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date thenTime;        //选择的时间     用于前台匹配显示(记)  2016-02-01

    private long thisDateId;    //选择的时间搓 用于后台查询某时间段或者 某天记录 20160201

    private String imgUrls;     //图片缩略图  9张

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date alarmTime;    //闹铃时间

    private int remindType;    //提醒类型  0正点 1提前5分钟 2 提前15 提前 30  提前1小时

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            //发布时间

    private String videoCover;        //视频封面

    private String videoUrl;        //视频地址

    private String users;        //用户ID组合：逗号分隔

    //与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔


}
