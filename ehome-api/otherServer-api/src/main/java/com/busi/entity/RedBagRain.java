package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 红包雨
 * @author: ZHaoJiaJie
 * @create: 2018-09-06 17:51
 */
@Setter
@Getter
public class RedBagRain {

    private long id;	//ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//用户Id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;	//添加时间

    private int pizeType;	//奖品种类：0谢谢参与  1现金

    private double quota;   	//具体金额

    //与数据库无关字段
    private String name; //用户名	查询后从内存获取最新
    private String head; //头像	查询后从内存获取最新
    private int proTypeId;	 //	省简称ID
    private long houseNumber;// 门牌号

}
