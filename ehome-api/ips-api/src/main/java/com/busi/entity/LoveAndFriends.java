package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 婚恋交友实体
 * author：zhaojiajie
 * create time：2018-8-1 16:20:06
 */
@Setter
@Getter
public class LoveAndFriends {

  private long id;//主键Id

  @Min(value= 1 ,message= "userId参数有误")
  private long userId;//用户Id

  @Length(max = 30, message = "标题最多可输入30字")
  private String title;//标题 （评分标准：字数<=5为5分  >=10为10分  >=15为20分(推荐)  >=20为30分 ）

  @Length(max = 140, message = "内容最多可输入140字")
  private String content;//内容 （评分标准：字数<=20为5分  >=50为10分  >=80为20分(推荐)  >=100为30分 ）

  private String imgUrl;//图片地址，最多九张 （评分标准：个数1为15分  >=3为30分(推荐)  >=6为40分）

  @Max(value = 2, message = "性别参数有误，未找到指定的性别选项")
  @Min(value= 1 ,message= "性别参数有误，未找到指定的性别选项")
  private int sex;//性别：1男，2女

  @Max(value = 6, message = "年龄参数有误，未找到指定的年龄选项")
  @Min(value= 1 ,message= "年龄参数有误，未找到指定的年龄选项")
  private int age;//年龄：1(18-29)，2（30—39），3（40-49），4（50-59），5（60-69）,6(>=70)

  @Max(value = 7, message = "身高参数有误，未找到指定的身高选项")
  @Min(value= 1 ,message= "身高参数有误，未找到指定的身高选项")
  private int stature;//身高(单位：cm)：1（<=139），2（140-149），3（150-159），4（160-169），5（170-179）,6(180-189),7(>=190)

  @Max(value = 8, message = "学历参数有误，未找到指定的学历选项")
  @Min(value= 1 ,message= "学历参数有误，未找到指定的学历选项")
  private int education;//学历:1中专,2专科,3本科,4双学士,5硕士,6博士,7博士后,8其他

  @Max(value = 4, message = "婚姻参数有误，未找到指定的婚姻选项")
  @Min(value= 1 ,message= "婚姻参数有误，未找到指定的婚姻选项")
  private int marriage;//婚姻：1已婚,2未婚,3离异,4丧偶

  @Max(value = 5, message = "收入参数有误，未找到指定的收入选项")
  @Min(value= 1 ,message= "收入参数有误，未找到指定的收入选项")
  private int income;//收入：0不限，1(<2000),2(2000-4999）,3（5000-9999）,4（10000-29999）,5（>30000）

  private int locationProvince;//所在省份

  private int locationCity;//所在市

  private int locationDistrict;//所在区

  private int likeNumber;//喜欢次数

  private int seeNumber;//浏览次数

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date refreshTime;//刷新时间

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date releaseTime;//发布时间

//  @Max(value = 3, message = "审核参数有误")
//  @Min(value= 1 ,message= "审核参数有误")
  private int auditType;//审核标志：1审核中，2通过，3未通过

//  @Max(value = 3, message = "删除参数有误")
//  @Min(value= 1 ,message= "删除参数有误")
  private int deleteType;//删除标志：1未删除，2用户删除，3管理人员删除

  private String delImgUrls;//将要删除的图片地址组合 “,”分隔

  private int fraction;//公告分数

}
