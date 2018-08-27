package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import java.util.Date;

/***
 * 支付绑定银行卡相关实体类
 * author：SunTianJie
 * create time：2018-8-16 09:22:52
 */
@Setter
@Getter
public class UserBankCardInfo {

  private long id;//主键ID

  private long userId;//用户ID

  @Length(max = 30, message = "bankCard参数超出合法范围")
  private String bankCard;//银行卡卡号

  @Length(max = 11, message = "bankPhone参数超出合法范围")
  private String bankPhone;//银行留存手机号

  @Length(max = 30, message = "bankName参数超出合法范围")
  private String bankName;//银行卡对应真实姓名

  @Length(max = 18, message = "bankCardNo参数超出合法范围")
  private String bankCardNo;//银行对应身份证号

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//入库时间 认证时间

  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

}
