package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @program: ehome
 * @description: 商品属性值实体类
 * @author: ZHaoJiaJie
 * @create: 2019-10-31 10:56
 */
@Setter
@Getter
public class GoodsBrandPropertyValue {

    private long id;//商品属性值id

    @Length(min = 1, max = 30, message = "value参数超出合法范围")
    private String value;//商品属性的值

    private long goodsBrandPropertyId;//商品属性id
}
