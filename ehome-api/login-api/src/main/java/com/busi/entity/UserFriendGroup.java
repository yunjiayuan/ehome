package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/***
 * 用户好友分组实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserFriendGroup {

  private long id;//主键ID

  @Min(value= 1 ,message= "userId参数有误")
  private long userId;//用户ID

  @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,10}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
  private String groupName;//分组名称 10个汉字  0：家园好友  -1：亲人，  -2：陌生人，  -3：黑名单

  @Min(value= 0 ,message= "分组类型groupType参数有误")
  @Max(value= 2 ,message= "分组类型groupType参数有误")
  private long groupType;//分组类型，0表示人物家园分组，1表示企业家园分组，2表示单位家园分组

}
