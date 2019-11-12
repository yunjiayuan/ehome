package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品对应特殊属性实体
 * @author: ZHaoJiaJie
 * @create: 2019-11-08 15:19
 */
@Setter
@Getter
public class GoodsOfSpecialProperty {

    private long id;//id

    private long goodsId;//商品id

    private String name;//商品特殊属性名称(多个属性之间"_"分隔)[规则：属性id,属性名称,属性值]
}
