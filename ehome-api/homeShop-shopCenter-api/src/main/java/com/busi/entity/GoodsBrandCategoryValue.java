package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 品牌主键id与分类主键id关系实体类
 * @author: ZHaoJiaJie
 * @create: 2019-06-10 14:14
 */
@Setter
@Getter
public class GoodsBrandCategoryValue {

    private long id;

    private long categoryId;  //分类ID

    private long brandId;  //品牌ID

    private String brandImg;  //品牌图标
}
