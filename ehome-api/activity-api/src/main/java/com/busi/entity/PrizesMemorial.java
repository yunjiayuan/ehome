package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 赢奖活动纪念奖奖品
 * @author: ZHaoJiaJie
 * @create: 2018-09-14 11:08
 */
@Setter
@Getter
public class PrizesMemorial {

    private long id;	//ID

    private String name;	//奖品名称

    private String describe;	//奖品描述

    private int issue;		//期号

    private double price;//奖品价值

    private String imgUrl;			//奖品图
}
