package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/***
 * 好友关系实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserRelationShip {

  private long id;//主键ID

  @Min(value= 1 ,message= "userId参数有误")
  private long userId;//当前登录者ID

  @Min(value= 1 ,message= "friendId参数有误")
  private long friendId;//好友ID

  @Min(value= 0 ,message= "好友类型friendType参数有误")
  @Max(value= 2 ,message= "好友类型friendType参数有误")
  private int friendType;//好友类型，0：家园好友（默认），1：企业好友，2：单位好友

  @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{0,10}",message = "备注名字格式有误，最大长度为10个汉字，并且不能包含非法字符")
  private String remarkName;//好友备注名  friendId在userId列表中的备注

  @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{0,10}",message = "备注名字格式有误，最大长度为10个汉字，并且不能包含非法字符")
  private String friendRemarkName;//好友备注名  friendId在userId列表中的备注

  @Min(value= -4 ,message= "分组ID groupId参数有误")
  private long groupId; //分组ID 0：家园好友-1：亲人，-2：陌生人，-3：黑名单 -4：新增特殊好友分组

  @Min(value= -4 ,message= "分组ID groupId参数有误")
  private long friendGroupId; //分组ID 0：家园好友-1：亲人，-2：陌生人，-3：黑名单 -4：新增特殊好友分组

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//添加时间

  private String name;//昵称

  private String head;//头像

  private int sex; // 性别

  private String birthday; // 生日

  private int proType; // 省简称

  private long houseNumber; // 门牌号

  private String gxqm;//个性签名

}
