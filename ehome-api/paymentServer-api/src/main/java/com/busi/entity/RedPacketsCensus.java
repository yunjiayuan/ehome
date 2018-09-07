package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 红包统计实体
 * author：SunTianJie
 * create time：2018/9/7 16:09
 */
@Getter
@Setter
public class RedPacketsCensus {

    private long id;//主键ID

    private long userId;//用户ID

    private long receivedCounts;//收到红包总数

    private long sendCounts;//发送的红包总数

    private double receivedAmount;//收到的红包总金额

    private double sendAmount;//发送的红包总金额
}
