package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description:鸟蛋奖品
 * @author: ZHaoJiaJie
 * @create: 2018-09-07 11:14
 */
@Setter
@Getter
public class BirdPrize {

    private long id;	//ID

    private int grade;	//奖品等级：1一等奖 2二等奖（后续添加）

    private int totalqQuota;	//总名额

    private String name;	//奖品名称

    private String describe;	//奖品描述

    private String startTime;	//开始时间(20170701)

    private String endTime;	//结束时间(20170715)

    private int issue;		//期号

    private int eggType;//蛋类型 0不限 1金蛋2 银蛋

    private double price;//奖品价值

    private String imgUrl;			//奖品图片(预留字段)

}
