package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/***
 * 用户个人资料界面的九张头像相册
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserHeadAlbum {

  private long id;//主键ID

  private long userId;//用户ID

  @Length(max = 1000, message = "imageUrl图片地址太长了")
  private String imageUrl;//图片地址拼接 逗号分隔

//  @Length(max = 100, message = "image2图片地址太长了")
//  private String image2;//第2张图片
//
//  @Length(max = 100, message = "image3图片地址太长了")
//  private String image3;//第3张图片
//
//  @Length(max = 100, message = "image4图片地址太长了")
//  private String image4;//第4张图片
//
//  @Length(max = 100, message = "image5图片地址太长了")
//  private String image5;//第5张图片
//
//  @Length(max = 100, message = "image6图片地址太长了")
//  private String image6;//第6张图片
//
//  @Length(max = 100, message = "image7图片地址太长了")
//  private String image7;//第7张图片
//
//  @Length(max = 100, message = "image8图片地址太长了")
//  private String image8;//第8张图片
//
//  @Length(max = 100, message = "image9图片地址太长了")
//  private String image9;//第9张图片

  private String delImageUrls;//将要删除的图片地址组合 “,”分隔

  private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

}
