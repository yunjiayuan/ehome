package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房订座信息实体
 * @author: ZhaoJiaJie
 * @create: 2019-06-26 15:24
 */
@Setter
@Getter
public class KitchenBooked {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long kitchenId;             // 厨房ID

    private int roomsTotal;             // 包间总数

    private int looseTableTotal;        // 散桌总数

    private int leastNumber;             // 包间最少人数

    private int mostNumber;             // 包间最多人数

    private String earliestTime;           // 最早营业时间

    private String latestTime;             // 最晚营业时间

    private int reserveDays;             // 可预订天数

    private String looseTable;             // 散桌 格式:人数,桌数;人数,桌数

    private String privateRoom;             // 包间 格式:人数,包间数;人数,包间数

}