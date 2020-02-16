package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

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

    @Length(max = 140, message = "whatAmIdoing不能超过140字")
    private String whatAmIdoing;            // 我在做什么

    //    private String whatIsDone;            // 为抗疫做了什么

    @Length(max = 30, message = "donateMoney不能超过30字")
    private String donateMoney;            // 捐钱

    @Length(max = 30, message = "捐物不能超过30字")
    private String benevolence;            // 捐物

    @Length(max = 30, message = "其他不能超过30字")
    private String other;            // 其他

    @Length(max = 30, message = "shoutSentence不能超过30字")
    private String shoutSentence;            // 为武汉喊句话

    //    private String later;            // 疫情过后

    @Length(max = 30, message = "imagine不能超过30字")
    private String imagine;            // 最想见的人

    @Length(max = 30, message = "wantToDo不能超过30字")
    private String wantToDo;            // 最想做的事

    @Length(max = 30, message = "wantToGo不能超过30字")
    private String wantToGo;            // 最想去的地方

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间
}
