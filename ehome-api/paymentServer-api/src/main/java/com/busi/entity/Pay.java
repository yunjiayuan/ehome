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
    @Max(value= 32 ,message= "serviceType参数有误，超出指定范围")
    private int serviceType;//业务类型 1:求助悬赏支付,2:求助购买支付,3:发个人红包,4:拆个人红包,5:钱包现金兑换家币,6:购买创始元老级会员支付,7:公告栏二手购买订单支付,
                            // 8:家门口厨房购买订单支付,9:购买元老级会员支付,10:购买普通会员支付,11:购买VIP高级会员支付,12:购买自频道会员支付,13购买靓号,14购买小时工，
                            // 15购买订座点菜支付，16缴纳开通楼店保证金订单支付，17楼店用户购买订单支付，18楼店商家补货订单支付，19医生律师订单支付,
                            // 20发送转账，21接收转账，22提现到微信，23提现到支付宝，24提现到银行卡，25支付旅游订单，26支付酒店民宿订单，27支付买药订单
                            // 28支付旅游、酒店民宿中的订座订单，29支付隐形商家中的商品订单，30支付找人倾诉中的订单，31支付需求汇中租房订单，32支付需求汇中购房订单

    @Length(min=16,max = 16, message = "orderNumber参数格式有误")
    @NotNull
    private String orderNumber;//订单编号

}
