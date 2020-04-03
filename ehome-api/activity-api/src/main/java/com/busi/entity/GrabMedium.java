package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 抢礼物记录
 * @author: ZHaoJiaJie
 * @create: 2020-04-03 10:00:03
 */
@Setter
@Getter
public class GrabMedium {
    private long id;    //ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//参与用户Id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;    //参与时间

    private int winningState;   //中奖状态 0没中 1中奖

    private int price;//奖品价值

    private String cost;//奖品名称

    //与数据库无关字段
    private String name; //用户名

    private String head; //头像

    private int proTypeId;     //	省简称ID

    private long houseNumber;// 门牌号
}
