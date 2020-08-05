package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 酒店房间实体
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 15:02:12
 */
@Setter
@Getter
public class HotelRoom {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long hotelId;          // 酒店ID

    private String name;       //名称

    private String describes;       //描述

    private String picture;        //图片

    private double cost;                // 价格

    private String squareMetre;                // 面积

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private int deleteType;            // 删除标志:0未删除,1用户删除,2管理员删除
}
