package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @program: ehome
 * @description: 酒店景区订座设置实体
 * @author: ZhaoJiaJie
 * @create: 2020-08-13 12:43:46
 */
@Setter
@Getter
public class KitchenReserveBooked {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long kitchenId;             // 景区、酒店ID

    private int reserveDays;             // 可预订天数

    @NotNull(message= "最早营业时间不能为空")
    private String earliestTime;           // 最早营业时间

    @NotNull(message= "最晚营业时间不能为空")
    private String latestTime;             // 最晚营业时间

    private int roomsTotal;             // 包间总数

    private int looseTableTotal;        // 散桌总数

    private int servingTimeType;        // 上菜时间类别  0默认  1自定义

    private int type;            // 所属类型：0酒店 1景区

}
