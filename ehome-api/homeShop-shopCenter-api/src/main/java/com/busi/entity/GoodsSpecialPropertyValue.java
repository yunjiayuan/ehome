package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品特殊属性值实体类
 * @author: ZHaoJiaJie
 * @create: 2019-11-08 15:26
 */
@Setter
@Getter
public class GoodsSpecialPropertyValue {

    private long id;//商品特殊属性值id

    private String specialValue;//商品特殊属性值的值

    private long specialPropertyId;//商品特殊属性id
}
