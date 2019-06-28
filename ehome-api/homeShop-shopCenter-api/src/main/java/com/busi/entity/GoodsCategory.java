package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品分类实体类
 * @author: ZHaoJiaJie
 * @create: 2019-06-04 16:57
 */
@Setter
@Getter
public class GoodsCategory {

    private long id;//商品分类id

    private String name;//商品分类名称

    private int goodCategoryId;//商品分类id(为0表示是父级，否则是子级)

    private int levelOne;//商品1级分类:0图书、音像、电子书刊  1手机、数码  2家用电器  3家居家装  4电脑、办公  5厨具  6个护化妆  7服饰内衣  8钟表  9鞋靴  10母婴  11礼品箱包  12食品饮料、保健食品  13珠宝  14汽车用品  15运动健康  16玩具乐器  17彩票、旅行、充值、票务

    private int levelTwo;//商品2级分类

    private int levelThree;//商品3级分类

    private int levelFour;//商品4级分类

    private int levelFive;//商品5级分类

    private String letter;//商品分类首字母

    //与数据库无关
    private String ids;  //分类组合id
    private int brand;  //是否包含品牌  0没有 1有
}
