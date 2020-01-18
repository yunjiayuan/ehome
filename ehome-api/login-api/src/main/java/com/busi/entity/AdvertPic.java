package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 过渡页广告图实体
 * author：SunTianJie
 * create time：2020-1-18 17:07:09
 */
@Setter
@Getter
public class AdvertPic {

  private String AdvertPicAddress;//客户端类型 1表示Android 2表示IOS

  private int showType;//0默认显示  1默认隐藏

}
