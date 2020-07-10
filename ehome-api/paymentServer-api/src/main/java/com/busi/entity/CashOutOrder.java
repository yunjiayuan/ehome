package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 提现订单实体
 * authorsuntj
 * Create time 2020/7/9 17:25
 */
@Getter
@Setter
public class CashOutOrder {

    private String id;//订单ID 程序生成

    private String openid;//用户标识  微信身份标识或支付宝身份标识

    private int type;//提现类型 0提现到微信 1提现到支付宝 2提现到银行卡（银联）

    @DecimalMax(value = "20000",message = "提现金额单笔最大不能超过5000元")
    @DecimalMin(value = "0.3",message = "提现金额单笔最小不能少于0.3元")
    private double money;//提现金额

    private long userId;//当前用户ID

    @Max(value = 1, message = "payStatus参数有误，超出指定范围")
    @Min(value= 0 ,message= "payStatus参数有误，超出指定范围")
    private int payStatus;//扣款状态  0未扣款 1已扣款

    @Max(value = 1, message = "payStatus参数有误，超出指定范围")
    @Min(value= 0 ,message= "payStatus参数有误，超出指定范围")
    private int cashOutStatus;//提现状态  0未到账 1已到账

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//提现发起时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date accountTime;//到账时间

}
