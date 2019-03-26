package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 自频道会员订单
 * @author: ZHaoJiaJie
 * @create: 2019-03-26 16:05
 */
@Setter
@Getter
public class SelfChannelVipOrder {

    private String orderNumber;//订单编号（程序生成）

    private long userId;//用户ID

    private double money;//花费金额

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//支付时间

    private int payState;//支付状态  0未支付   1已支付

}
