package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 支付实体 与数据库无关实体
 * author：SunTianJie
 * create time：2018/8/28 14:40
 */
@Getter
@Setter
public class Pay {

    @Min(value= 1 ,message= "userId参数格式有误")
    private long userId;

//    @Length(min=32,max = 32, message = "payPassword参数格式有误")
//    @NotNull
    private String payPassword;//支付密码

    @Length(min=16,max = 16, message = "paymentKey参数格式有误")
    @NotNull
    private String paymentKey;//支付私钥

    @Min(value= 1 ,message= "serviceType参数有误，超出指定范围")
    @Max(value= 12 ,message= "serviceType参数有误，超出指定范围")
    private int serviceType;//业务类型 1:求助悬赏支付,2:求助购买支付,3:发个人红包,4:拆个人红包,5:钱包现金兑换家币,6:购买创始元老级会员支付,7:公告栏二手购买订单支付,8:家门口厨房购买订单支付,9:购买元老级会员支付,10:购买普通会员支付,11:购买VIP高级会员支付,12:购买自频道会员支付

    @Length(min=16,max = 16, message = "orderNumber参数格式有误")
    @NotNull
    private String orderNumber;//订单编号

}
