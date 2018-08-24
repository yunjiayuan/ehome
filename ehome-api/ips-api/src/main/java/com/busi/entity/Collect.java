package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 公告收藏实体
 * @author: ZHaoJiaJie
 * @create: 2018-8-24 15:05:31
 */
@Setter
@Getter
public class Collect {

  private long id;		//主见ID

  @Min(value= 1 ,message= "myId参数有误")
  private long myId;		//收藏用户ID

  private long infoId;	//公告详情ID

  private int afficheType;	//公告类别标志

  @Length(max = 30, message = "标题最多可输入30字")
  private String title;	//公告标题

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;		//收藏时间

}
