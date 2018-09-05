package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 喂鸟记录
 * @author: ZHaoJiaJie
 * @create: 2018-09-04 13:48
 */
@Setter
@Getter
public class BirdFeedingRecord {

    private long id;        //ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;    //登陆者

    private long visitId;    //被喂者

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;        //喂鸟时间

    //数据库无关字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date birthday; // 生日  用来算年龄

    private int sex;    //性别

    private long feedBirdTotalCount;    //互动次数
}
