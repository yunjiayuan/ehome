package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 客户端提现参数实体
 * authorsuntj
 * Create time 2020/7/9 17:25
 */
@Getter
@Setter
public class CashOut {

    private int type;//提现类型 0提现到微信 1提现到支付宝 2提现到银行卡（银联）

    @Max(value = 5000, message = "提现金额单笔最大不能超过5000元")
//    @Min(value= 1 ,message= "提现金额单笔最小不能少于1元")
    private double money;//提现金额

    private long userId;//当前用户ID

}
