package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
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

  @Pattern(regexp="^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$",message = "手机号格式有误，请输入正确的手机号")
  private String bankPhone;//银行留存手机号

  @Length(max = 30, message = "bankName参数超出合法范围")
  private String bankName;//银行卡对应真实姓名

  @IdCardConstraint(message = "身份证格式有误")
  private String bankCardNo;//银行对应身份证号

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//绑定时间

  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

}
