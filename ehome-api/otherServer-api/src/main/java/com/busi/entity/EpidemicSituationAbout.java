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

//    private String whatIsDone;            // 为抗疫做了什么

    private String donateMoney;            // 捐钱

    private String benevolence;            // 捐物

    private String other;            // 其他

    private String shoutSentence;            // 为武汉喊句话

//    private String later;            // 疫情过后

    private String imagine;            // 最想见的人

    private String wantToDo;            // 最想做的事

    private String wantToGo;            // 最想去的地方

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间
}
