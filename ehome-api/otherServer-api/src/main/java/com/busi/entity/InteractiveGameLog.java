package com.busi.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 互动游戏胜负记录实体类
 * @author: suntj
 * @create: 2018-8-22 09:29:37
 */
@Setter
@Getter
public class InteractiveGameLog {

  private long id;//主键ID

  @Min(value = 1, message = "myId参数有误")
  private long myId;//邀请者用户ID

  @Min(value = 1, message = "userId参数有误")
  private long userId;//参与者用户ID

  @Max(value = 9999, message = "homePoint参数有误")
  @Min(value = 1, message = "homePoint参数有误")
  private int homePoint;//赌注：家点

  @Min(value = 0, message = "gameResults参数有误")
  private long gameResults;//游戏结果 0表示平局  >0表示胜利者的用户ID

  @Max(value = 1, message = "gameType参数有误")
  @Min(value = 0, message = "gameType参数有误")
  private int gameType;//游戏类型 0表示骰子游戏 1表示猜拳游戏

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date time;//时间

  private int myPoint;//邀请者点数 与数据库无关字段

  private int userPoint;//参与者点数 与数据库无关字段

}
