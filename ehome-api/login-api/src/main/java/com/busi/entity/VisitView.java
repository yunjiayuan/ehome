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

  private long myId;//当前登录用户ID（用于脚印更新）

  private long userId;//被访问用户ID

  private long todayVisitCount;//今天访问量  停用数据库存储 改存在redis缓存中 每日0点缓存清空

  private long totalVisitCount;//总访问量

}
