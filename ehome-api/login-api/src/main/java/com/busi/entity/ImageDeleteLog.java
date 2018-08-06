package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;
import java.util.Date;

/***
 * 图片删除记录体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class ImageDeleteLog {

  private long id;//主键ID

  private long userId;//主键ID

  @Length(max = 100, message = "imageUrl参数有误，图片地址太长")
  @NotNull(message = "imageUrl参数有误，图片地址不能为空")
  private String imageUrl;//将要被删除的图片地址

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date time;//添加时间  用于定时清理依据

}
