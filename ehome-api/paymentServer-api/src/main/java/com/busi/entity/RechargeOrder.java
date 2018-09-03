package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 充值订单实体 与数据库无关
 * author：SunTianJie
 * create time：2018/8/28 11:05
 */

@Getter
@Setter
public class RechargeOrder {

    private String orderNumber;//订单编号（程序生成）

    private long userId;//用户ID

    private long money;//充值金额金额

    private int payStatus;//支付状态  0未支付 1已支付

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//订单生成时间

}
