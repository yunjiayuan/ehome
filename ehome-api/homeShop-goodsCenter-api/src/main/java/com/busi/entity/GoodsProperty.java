package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品对应属性实体
 * @author: ZHaoJiaJie
 * @create: 2019-11-01 14:34
 */
@Setter
@Getter
public class GoodsProperty {

    private long id;//id

    private long goodsId;//商品id

    private String name;//商品属性名称(多个属性之间"_"分隔)[规则：属性id,属性名称,属性值]

//    private String value;//商品属性的值(多个属性之间"_"分隔)
}
