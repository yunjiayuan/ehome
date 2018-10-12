package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: 节日实体类
 * @description:
 * @author: ZHaoJiaJie
 * @create: 2018-10-10 15:40:18
 */
@Setter
@Getter
public class NotepadFestival {

  private long id;

  private long thisYearId;		//年份  例如：2016

  private String newYearsDays;	//元旦

  private String springFestivals;	//春节

  private String qingMingFestival;//清明节

  private String laborDay;		//劳动节

  private String dragonBoatFestival;	//端午节

  private String midAutumnFestival;	//中秋节

  private String nationalDay;			//国庆节

  private String overtimeDays;		//加班日

  private String holidayDays;		//假期日(格式：逗号分隔)

}
