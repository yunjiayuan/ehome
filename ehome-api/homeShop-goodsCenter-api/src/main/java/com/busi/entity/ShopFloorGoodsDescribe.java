package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 楼店商品描述
 * @author: ZHaoJiaJie
 * @create: 2019-11-20 14:16
 */
@Setter
@Getter
public class ShopFloorGoodsDescribe {

    private long id;                    //主键

    private long userId;                //用户

    private String imgUrl;              //图片

    private String content;             //内容

    // 与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔
}
