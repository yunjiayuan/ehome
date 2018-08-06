package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 访问量信息实体
 * author：SunTianJie
 * create time：2018/7/6 16:15
 */
@Setter
@Getter
public class VisitView {

  private long id;//主键ID

  private long userId;//用户ID

  private long todayVisitCount;//今天访问量

  private long totalVisitCount;//总访问量

}
