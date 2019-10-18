package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品分类
 * @author: ZHaoJiaJie
 * @create: 2019-10-14 13:39
 */
@Setter
@Getter
public class GoodsSort {

    private long id;                    //主键

    private long shopId;                //店铺ID

    private long userId;                //用户

    private String sortName;            //分类名称

    private String superior;            //分类归属名称

    private long superiorId;            //分类归属ID
}
