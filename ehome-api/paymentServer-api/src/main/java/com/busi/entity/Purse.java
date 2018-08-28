package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Min;
import java.util.Date;

/***
 * 钱包信息实体类
 * author：SunTianJie
 * create time：2018-8-16 09:22:52
 */
@Setter
@Getter
public class Purse {

  private long id;//主键ID

  private long userId;//用户ID

  @Min(value= 0 ,message= "homeCoin参数有误，超出指定范围")
  private long homeCoin;//家币

  @Min(value= 0 ,message= "homePoint参数有误，超出指定范围")
  private long homePoint;//家点

  @Min(value= 0 ,message= "spareMoney参数有误，超出指定范围")
  private double spareMoney;//人民币

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//开户时间

  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

}
