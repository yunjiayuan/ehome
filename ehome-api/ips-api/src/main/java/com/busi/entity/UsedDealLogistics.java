package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 二手物流信息
 * @author: ZHaoJiaJie
 * @create: 2018-10-24 13:54:08
 */
@Setter
@Getter
public class UsedDealLogistics {

    private long id;//主键ID

    private long userId;//商家ID

    private long myId;//买家ID

    private int brand;//订单物流方式

    private String brandName;//订单物流名称

    private String no;//订单编号

    private String status;

    private String data;//物流具体信息，格式：时间1&&信息1##时间2&&信息2##时间3&&信息3   例如：2017-08-21 13:42:30&&卖家已发货

    private String orders;

}
