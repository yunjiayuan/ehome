package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 过渡页广告图实体
 * author：SunTianJie
 * create time：2020-1-18 17:07:09
 */
@Setter
@Getter
public class AdvertPic {

  private String advertPicAddress;//图片地址

  private int type;//0表示IOS 1表示安卓

  private int version;//过渡页最新版本号  客户端根据服务端版本号动态更换本地过渡页广告图

}
