package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/***
 * 支付密码相关实体类
 * author：SunTianJie
 * create time：2018-8-16 09:22:52
 */
@Setter
@Getter
public class PursePayPassword {

  private long id;//主键ID

  private long userId;//用户ID

  @Length(min=32,max = 32, message = "payPassword参数超出合法范围")
  private String payPassword;//支付密码  数据库中对应的密码

  @Length(max = 6, message = "payCode参数超出合法范围")
  private String payCode;//支付加盐码

  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

  @Length(min=32,max = 32, message = "newPayPassword参数超出合法范围")
  private String newPayPassword;//新支付密码  与数据库无关字段 只用于修改操作

  @Length(min=16,max = 16, message = "paymentKey参数超出合法范围")
  private String paymentKey;//16位重置秘钥  10分钟有效  使用后失效

  @Length(min=4,max = 4 ,message = "code参数超出合法范围")
  private String code;//验证码 与数据库无关字段 只用于密码重置操作

}
