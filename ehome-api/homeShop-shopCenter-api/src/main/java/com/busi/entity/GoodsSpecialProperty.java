package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品特殊属性表
 * @author: ZHaoJiaJie
 * @create: 2019-11-08 15:26
 */
@Setter
@Getter
public class GoodsSpecialProperty {

    private long id;//商品特殊属性id

    private String name;//商品特殊属性名称

    private int goodCategoryId;//商品分类id
}
