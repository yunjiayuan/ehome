package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 排挡视频
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 16:38
 */
@Setter
@Getter
public class SelfChannel {

    private long id;

    private long userId;   //用户

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;  //排挡时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date addtime;  //新增时间

    private int selectionType;//活动分类 0云视频  (后续添加)

    private int province;//省ID

    private int city;//市ID

    private int district;//区ID

    private String singer;//演唱者

    private String songName;//歌名

    private int duration;//时长

    private String birthday;//出生日期

    private String videoUrl;//视频地址

    private String videoCover;//活动封面
}
