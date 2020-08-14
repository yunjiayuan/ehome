package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 厨房上菜时间实体
 * @author: ZHaoJiaJie
 * @create: 2019-08-22 13:28
 */
@Setter
@Getter
public class KitchenReserveServingTime {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long kitchenId;             // 酒店/景区ID

    private String upperTime;        //上菜时间

    private int type;            // 所属类型：0酒店 1景区
}
