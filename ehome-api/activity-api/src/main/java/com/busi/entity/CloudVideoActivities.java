package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 云视频活动
 * @author: ZHaoJiaJie
 * @create: 2019-03-20 15:36
 */
@Setter
@Getter
public class CloudVideoActivities {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//用户ID

    private int selectionType;//活动分类 0云视频  (后续添加)

    private int province;//省ID

    private int city;//市ID

    private int district;//区ID

    private String singer;//演唱者

    private String songName;//歌名

    private String duration;//时长

    private int sex;//性别 1男2女

    private String birthday;//出生日期

    private long votesCounts;//票数

    private int activityState;//活动状态：0进行中  1已结束

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//参加时间

    private String videoUrl;//视频地址

    private String videoCover;//活动封面

    //与数据库无关字段
//    private String name; //用户名
//
//    private String head; //头像
//
//    private int proTypeId;//省简称ID
//
//    private long houseNumber;//门牌号

}
