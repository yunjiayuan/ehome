package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 楼店保证金订单
 * @author: ZHaoJiaJie
 * @create: 2019-11-13 10:52
 */
@Setter
@Getter
public class ShopFloorBondOrders {

    private String orderNumber;//订单编号（程序生成）

    private long userId;//用户ID

    private double money;//花费金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//支付时间

    private int payState;//支付状态  0未支付   1已支付
}
