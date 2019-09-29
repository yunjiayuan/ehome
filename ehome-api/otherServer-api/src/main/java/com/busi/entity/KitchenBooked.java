package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @program: ehome
 * @description: 厨房订座设置实体
 * @author: ZhaoJiaJie
 * @create: 2019-06-26 15:24
 */
@Setter
@Getter
public class KitchenBooked {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long kitchenId;             // 厨房ID

    private int reserveDays;             // 可预订天数

    @NotNull(message= "最早营业时间不能为空")
    private String earliestTime;           // 最早营业时间

    @NotNull(message= "最晚营业时间不能为空")
    private String latestTime;             // 最晚营业时间

    private int roomsTotal;             // 包间总数

    private int looseTableTotal;        // 散桌总数

    private int servingTimeType;        // 上菜时间类别  0默认  1自定义

//    private int leastNumber;             // 包间最少人数
//
//    private int mostNumber;             // 包间最多人数

//    private String looseTable;             // 散桌 格式:人数,桌数;人数,桌数
//
//    private String privateRoom;             // 包间 格式:人数,包间数;人数,包间数

}
