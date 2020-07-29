package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * @program: ehome
 * @description: 景区门票实体
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 10:41:32
 */
@Setter
@Getter
public class ScenicSpotTickets {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long scenicSpotId;          // 景区ID

    private String name;       //名称

    private double cost;                // 价格

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private int deleteType;            // 删除标志:0未删除,1用户删除,2管理员删除
}
