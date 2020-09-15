package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 抽签实体
 * @author: ZhaoJiaJie
 * @create: 2020-09-14 19:55:52
 */
@Setter
@Getter
public class Drawings {

    private long id;    //ID

    private String name;//名称

    private String grade;//等级，例：上上签、下下签

    private String contents;//内容

    //与数据库无关字段
    private int num;    //剩余次数
}
