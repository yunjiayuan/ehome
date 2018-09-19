package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description:赢奖活动奖品
 * @author: ZHaoJiaJie
 * @create: 2018-09-14 11:04
 */
@Setter
@Getter
public class PrizesEvent {

    private long id;	//ID

    private int grade;	//奖品等级：1一等奖 2纪念奖

    private int totalqQuota;	//总名额

    private String name;	//奖品名称

    private String describe;	//奖品描述

    private String startTime;	//开始时间(20170701)

    private String endTime;	//结束时间(20170707)

    private int issue;		//期号

    private int price;//奖品价值

    private String imgUrl;			//奖品图

}
