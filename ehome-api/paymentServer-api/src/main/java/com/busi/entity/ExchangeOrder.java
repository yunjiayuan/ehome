package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 兑换订单实体 与数据库无关
 * author：SunTianJie
 * create time：2018/8/28 11:05
 */

@Getter
@Setter
public class ExchangeOrder {

    private String orderNumber;//订单编号（程序生成）

    private long userId;//用户ID

    private long money;//兑换金额

    private int exchangeType;//兑换类型  当exChangeType==1时人民币兑换家币 当exChangeType==2时家币兑换家点

    private int payStatus;//支付状态  0未支付 1已支付

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//订单生成时间

}
