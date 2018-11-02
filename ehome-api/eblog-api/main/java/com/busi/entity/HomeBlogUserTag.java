package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @program: ehome
 * @description: 生活圈兴趣标签
 * @author: ZHaoJiaJie
 * @create: 2018-11-02 16:58
 */
@Setter
@Getter
public class HomeBlogUserTag {

    private long id;                //主建ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;            //用户

    private String tags;            //标签ID组  0,12,3

}
