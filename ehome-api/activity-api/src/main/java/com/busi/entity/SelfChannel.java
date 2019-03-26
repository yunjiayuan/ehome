package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 自频道
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 16:38
 */
@Setter
@Getter
public class SelfChannel {

    private long id;

    private long userId;   //用户

    private int time;  //新增时间 （20190325121212）

    private Date addtime;  //新增时间 （20190325121212）

    private int selectionType;//活动分类 0云视频  (后续添加)

    private int province;//省ID

    private int city;//市ID

    private int district;//区ID

    private String singer;//演唱者

    private String songName;//歌名

    private String duration;//时长

    private int sex;//性别 1男2女

    private String birthday;//出生日期

    private String videoUrl;//视频地址

    private String videoCover;//活动封面
}
