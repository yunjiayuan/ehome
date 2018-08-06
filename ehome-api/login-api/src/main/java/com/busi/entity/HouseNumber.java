package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 门牌号记录实体
 * author：SunTianJie
 * create time：2018/6/28 9:40
 */
@Setter
@Getter
public class HouseNumber {

  private long id;//主键

  private int proKeyWord;//省简称ID

  private long newNumber;//门牌号最新记录值

}
