package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 黑店统计实体
 * @author: ZHaoJiaJie
 * @create: 2020-06-10 15:07:30
 */
@Setter
@Getter
public class ShopFloorStatistics {

    private long id;                    //主键

    private int province;                //省份

    private int city;                    //城市

    private int number;                  //数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            //刷新时间
}
