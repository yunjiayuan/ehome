package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 群消息设置
 * @author: ZHaoJiaJie
 * @create: 2018-09-06 17:51
 */
@Setter
@Getter
public class Groupsetup {

  private long id;

  @Min(value = 1, message = "userId参数有误")
  private long userId;//用户

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date addTime;	//添加时间

  private long groupId;//群ID

  private long setup;//群消息设置  ：0接受消息并提醒 1接受消息但不提醒    2屏蔽群消息

}
