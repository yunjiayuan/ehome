package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 永辉买菜分类实体
 * @author: ZHaoJiaJie
 * @create: 2019-11-15 10:46
 */
@Setter
@Getter
public class YongHuiGoodsSort {

    private long id;//商品分类id

    private String name;//名称

    private int levelOne;//商品1级分类

    private int levelTwo;//商品2级分类

    private String letter;//商品分类首字母

    private String picture;//商品图片

    private int enabled;//是否启用此类型 0默认启用 1关闭此类型

}
