package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/***
 * 钱包出入账明细实体类
 * author：SunTianJie
 * create time：2018-8-16 09:22:52
 */
@Setter
@Getter
public class PurseChangingLog {

  private long id;//主键ID

  private long userId;//用户ID

  @Max(value = 19, message = "tradeType参数有误，超出指定范围")
  @Min(value= -1 ,message= "tradeType参数有误，超出指定范围")
  private int tradeType;//交易类型 0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出,,18游戏支出，19游戏转入，20任务奖励转入

  @Max(value = 2, message = "currencyType参数有误，超出指定范围")
  @Min(value= -1 ,message= "currencyType参数有误，超出指定范围")
  private int currencyType;//交易支付类型 0钱(真实人民币),1家币,2家点

  private double tradeMoney;//交易金额

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//交易时间

}
