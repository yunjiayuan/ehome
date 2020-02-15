package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 我和疫情
 * author ZJJ
 * Create time 2020-02-15 16:02:25
 */
@Setter
@Getter
public class EpidemicSituationAbout {

    private long id;//主键ID

    private long userId;                // 用户ID

    private double lat;                    //纬度

    private double lon;                    //经度

    private String address;            // 详细地址

    private String whatAmIdoing;            // 我在做什么

    private String whatIsDone;            // 为抗疫做了什么

    private String shoutSentence;            // 为武汉喊句话

    private String later;            // 疫情过后

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间
}
