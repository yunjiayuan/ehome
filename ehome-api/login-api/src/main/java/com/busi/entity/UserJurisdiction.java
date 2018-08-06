package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/***
 * 用户权限实体类（设置功能中的权限设置 包括房间锁和被访问权限）
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserJurisdiction {

  private long id;//主键ID

  private long userId;//用户ID

  @Max(value = 2, message = "花园房间锁参数有误，超出指定范围")
  @Min(value= 0 ,message= "花园房间锁参数有误，超出指定范围")
  private long garden;//花园锁状态 1未上锁 2已上锁

  @Max(value = 2, message = "客厅房间锁参数有误，超出指定范围")
  @Min(value= 0 ,message= "客厅房间锁参数有误，超出指定范围")
  private long livingRoom;//客厅锁状态 1未上锁 2已上锁

  @Max(value = 2, message = "家店房间锁参数有误，超出指定范围")
  @Min(value= 0 ,message= "家店房间锁参数有误，超出指定范围")
  private long homeStore;//家店锁状态 1未上锁 2已上锁

  @Max(value = 2, message = "存储室房间锁参数有误，超出指定范围")
  @Min(value= 0 ,message= "存储室房间锁参数有误，超出指定范围")
  private long storageRoom;//存储室锁状态 1未上锁 2已上锁

  @Max(value = 3, message = "被访问权限参数有误，超出指定范围")
  @Min(value= 0 ,message= "被访问权限参数有误，超出指定范围")
  private long accessRights;//自己家被访问权限 1允许任何人  2禁止任何人  3 仅好友访问权限

  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

}
