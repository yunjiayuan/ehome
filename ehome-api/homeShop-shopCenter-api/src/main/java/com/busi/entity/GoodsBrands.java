package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品品牌实体类
 * @author: ZHaoJiaJie
 * @create: 2019-06-10 14:12
 */
@Setter
@Getter
public class GoodsBrands {

    private long id;//品牌id

    private String name;//品牌名称（去掉'的）

    private String letter;//品牌名首字母

    private String realname;//真实品牌名称

}
