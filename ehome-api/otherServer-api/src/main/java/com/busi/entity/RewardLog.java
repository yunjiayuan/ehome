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
 * 用户奖励统计记录总表 包含红包雨奖励、新用户注册奖励、生活圈小视频奖励等其他活动类奖励
 */
@Setter
@Getter
public class RewardLog {

  private long id;//主键ID

  @Min(value = 1, message = "userId参数有误")
  private long userId;//用户ID

  @Min(value = 0, message = "rewardType参数有误，数值超出指定范围")
  @Max(value = 14, message = "rewardType参数有误，数值超出指定范围")
  private int rewardType;//奖励类型 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
                         // 7是一级稿费作品 8是二级稿费作品 9是三级稿费作品 10是四级稿费作品 11邀请商家入驻奖励（订座） 12邀请商家入驻奖励（旅游）
                         // 13邀请商家入驻奖励（酒店民宿） 14邀请商家入驻奖励（药店）

  @Min(value = 0, message = "rewardMoneyType参数有误，数值超出指定范围")
  @Max(value = 0, message = "rewardMoneyType参数有误，数值超出指定范围")
  private int rewardMoneyType;//奖励金额类型 0表示人民币  1表示其他（预留）

  @DecimalMax(value = "8888",message = "rewardMoney参数有误，已超出最大奖励金额")
  @DecimalMin(value = "0.00",message = "rewardMoney参数有误，奖励金额不能小于0")
  private double rewardMoney;//奖励的具体金额

  @Min(value = 0, message = "isNew参数有误，数值超出指定范围")
  @Max(value = 1, message = "isNew参数有误，数值超出指定范围")
  private int isNew;//是否为新的 0未读新记录 1已读记录

  @Min(value = 0, message = "infoId参数有误，数值超出指定范围")
  private long infoId;//额外字段，主要用于业务ID的存储，目前只存储生活圈的主键ID

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date time;//奖励时间

}
