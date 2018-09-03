package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 *  购买会员订单实体
 * author：SunTianJie
 * create time：2018/9/3 18:44
 */
@Setter
@Getter
public class MemberOrder {

    private String orderNumber;//订单编号（程序生成）

    private long userId;//用户ID

    private double money;//花费金额

    private int monthNumber;//购买的月数

    private int deductMonthNumber;//升级时抵扣的普通会员的月数 仅当payType=0和expireType=3时有效

    private int expireType;//将要购买的会员类型 0表示购买创始元老级会员  1购买元老级会员  2表示购买普通会员  3表示购买高级会员

    private int payType;//购买方式  0直接购买  1升级购买  此参数只在expireType=2或expireType=3时有效

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//支付时间

    private int payState;//支付状态  0未支付   1已支付
}
