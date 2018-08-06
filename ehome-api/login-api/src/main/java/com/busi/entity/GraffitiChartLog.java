package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/***
 * 涂鸦记录实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class GraffitiChartLog {

  private long id;//主键ID

  @Min(value= 1 ,message= "myId参数有误")
  private long myId;//被涂鸦者

  @Min(value= 1 ,message= "userId参数有误")
  private long userId;//涂鸦用户

  @NotNull
  private String graffitiHead;//涂鸦头像地址

  @NotNull
  private String graffitiContent;//涂鸦的文本信息

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//涂鸦时间

  private String name;//用户名

  private String head;//用户头像

}
