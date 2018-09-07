package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 中奖记录
 * @author: ZHaoJiaJie
 * @create: 2018-09-06 17:51
 */
@Setter
@Getter
public class BirdTheWinners {

    private long id;	//ID

    @Min(value = 1, message = "myId参数有误")
    private long userId;//中奖用户Id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;	//中奖时间

    private int eggType;//蛋类型 0不限 1金蛋2 银蛋

    private int grade;	//奖品类型：0悲催蛋  1艳遇蛋  2家点红包 3家币红包 4现金红包 5一等奖 6二等奖（后续添加）

    private int issue;		//期号

    private double cost;//奖品价值

    private int awards;	//奖品ID

    //与数据库无关字段
    private String name; //用户名	查询后从内存获取最新
    private String head; //头像	查询后从内存获取最新
    private int proTypeId;	 //	省简称ID
    private long houseNumber;// 门牌号

}
