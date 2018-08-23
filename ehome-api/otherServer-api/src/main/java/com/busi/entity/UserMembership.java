package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/***
 * 用户会员信息实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserMembership {

  private long id;//用户主键ID

  private long userId;//用户ID

  @Max(value = 1, message = "membershipLevel参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "membershipLevel参数有误，参数值超出合法范围")
  private int membershipLevel;//特殊会员：元老级会员  0：默认非元老级会员  1：元老级会员(100元会员)

  @Max(value = 5, message = "initiatorMembershipLevel参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "initiatorMembershipLevel参数有误，参数值超出合法范围")
  private int initiatorMembershipLevel;//特殊会员：创始元老级会员 等级 0：默认非创始元老级会员 1:一级创始元老级会员（100元）2:二级创始元老级会员（200元）3:三级创始元老级会员（300元）4:四级创始元老级会员（400元）5:五级创始元老级会员（500元）

  @Max(value = 1, message = "vipMembershipLevel参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "vipMembershipLevel参数有误，参数值超出合法范围")
  private int vipMembershipLevel;//VIP高级会员等级  0：默认非会员 1：一级高级会员

  @Max(value = 1, message = "regularMembershipLevel参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "regularMembershipLevel参数有误，参数值超出合法范围")
  private int regularMembershipLevel;//普通会员等级  0：默认非会员 1：一级普通会员

  @Max(value = 4, message = "memberShipStatus参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "memberShipStatus参数有误，参数值超出合法范围")
  private int memberShipStatus;//用户当前会员状态  1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员

  @Max(value = 1, message = "memberShipLevelStatus参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "memberShipLevelStatus参数有误，参数值超出合法范围")
  private int memberShipLevelStatus;//元老级会员返现状态：0未返现 1已返现

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date regularExpireTime;//普通会员到期时间

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date regularStopTime;//普通会员临时中止时间 后边会根据业务再次开启

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date vipExpireTime;//高级会员到期时间

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date vipStopTime;//高级会员临时中止时间 后边会根据业务再次开启

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date membershipTime;//元老级会员开通时间

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date initiatorMembershipTime;//创始元老级会员开通时间

  @Max(value = 1, message = "redisStatus参数有误，参数值超出合法范围")
  @Min(value= 0 ,message= "redisStatus参数有误，参数值超出合法范围")
  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据库已有对应数据  与数据无关字段

}
