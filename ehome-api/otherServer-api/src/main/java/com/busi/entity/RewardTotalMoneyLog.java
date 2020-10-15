package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

/**
 * 用户奖励统计记录总表 包含红包雨奖励、新用户注册奖励、生活圈小视频奖励等其他活动类奖励
 */
@Setter
@Getter
public class RewardTotalMoneyLog {

  private long id;//主键ID

  @Min(value = 1, message = "userId参数有误")
  private long userId;//用户ID

  @DecimalMax(value = "9999999",message = "rewardMoney参数有误，已超出最大奖励金额")
  @DecimalMin(value = "0.00",message = "rewardMoney参数有误，奖励总金额不能小于0")
  private double rewardTotalMoney;//奖励总金额

  private String userName;//用户名字  与数据库无关
  private String userHead;//用户头像  与数据库无关
  private long houseNumber;//用户门牌号  与数据库无关
  private int proId;//用户省简称ID  与数据库无关

}
