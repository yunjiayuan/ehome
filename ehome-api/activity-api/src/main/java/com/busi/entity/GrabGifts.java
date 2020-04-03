package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 抢礼物奖品
 * @author: ZHaoJiaJie
 * @create: 2020-04-03 10:00:03
 */
@Setter
@Getter
public class GrabGifts {
    private long id;    //ID

    private String name;    //奖品名称

    private String describe;    //奖品描述

    private int price;//奖品价值

    private String imgUrl;            //奖品图

    private int number;    //数量

    //与数据库无关字段
    private String music;    //音乐地址

    private int num;    //剩余次数
}
