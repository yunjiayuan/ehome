package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 标签成员
 * @author: ZHaoJiaJie
 * @create: 2018-11-01 17:06
 */
@Setter
@Getter
public class HomeBlogAccessMembers {

    //与数据库无关字段
    private long userId;        //成员


    private String name;    //用户名
    private String head;    //头像

}
