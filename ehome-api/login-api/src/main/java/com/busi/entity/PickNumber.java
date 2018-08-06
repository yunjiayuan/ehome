package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 靓号、预售账号、预选账号记录实体
 * author：SunTianJie
 * create time：2018/6/5 9:30
 */
@Getter
@Setter
public class PickNumber{

  private long id;//主键ID

  private long houseNumber;//门牌号

  private long proId;//省简称ID

  private long isGoodNumber;//是否为靓号 1为靓号，0为非靓号

  private long isVipNumber;//是否为VIP 1为VIP号，0不是VIP

  private Date time;//添加时间

}
