package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

/**
 * @program: ehome
 * @description: 菜品分类实体
 * @author: ZHaoJiaJie
 * @create: 2019-07-31 14:13
 */
@Setter
@Getter
public class KitchenDishesSort {

    private long id;                    // 主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 用户

    private long kitchenId;             // 厨房、景区、酒店ID

    @Length(max = 8, message = "分类名称不能超过8字")
    private String name;                // 分类名

    private int bookedState;            //   0厨房  1订座  2酒店  3景区

}
