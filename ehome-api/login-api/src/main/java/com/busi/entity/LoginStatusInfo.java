package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/***
 * 用户登录状态记录信息
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class LoginStatusInfo {

  private long id; //主键ID

  private long userId;//用户ID

  private String clientType;//设备类型 如 iphone 小米 华为

  private String clientModel;//设备型号 如6S 、7S、 R19等

  private String clientSystemModel;//设备系统型号 如IOS10.3.1 安卓4.1.3

  private String serverVersion;//服务端的版本号

  private String appVersion;//客户端设备的APP版本号

  private String ip;//客户端登录设备的IP

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//登录时间

}
