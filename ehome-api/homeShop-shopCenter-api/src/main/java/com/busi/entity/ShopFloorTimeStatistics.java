package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 黑店统计实体（按时间）
 * @author: ZHaoJiaJie
 * @create: 2020-07-06 16:25:40
 */
@Setter
@Getter
public class ShopFloorTimeStatistics {

    private long id;                    //主键

    private int distributionState;        //状态  0未配货  1已配货

    private int province;                //省份

    private int city;                    //城市

    private int number;                  //数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            //时间
}
