package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 商品描述
 * @author: ZHaoJiaJie
 * @create: 2019-10-14 13:50
 */
@Setter
@Getter
public class GoodsDescribe {

    private long id;                    //主键

    private long userId;                //用户

    private long shopId;                //店铺ID

    private String imgUrl;              //图片

    private String content;             //内容

    // 与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔
}
