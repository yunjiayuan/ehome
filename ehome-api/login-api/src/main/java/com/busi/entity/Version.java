package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 版本号信息实体
 * author：SunTianJie
 * create time：2018/7/6 16:15
 */
@Setter
@Getter
public class Version {

  private long id;//主键

  private int clientType;//客户端类型 1表示Android 2表示IOS

  private int updateType;//更新类型  0表示普通更新用户可以跳过，1表示强制更新，用户不更新不能进行登录

  private int version;//最新版本号

}
