package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 二货商城（家店）购物车
 * @author: ZhaoJiaJie
 * @create: 2020-07-13 13:19:50
 */
@Setter
@Getter
public class HomeShopShoppingCart {

    private long id;                    //主键

    private long userId;                //用户

    private long goodsId;                //商品ID

    private String goodsCoverUrl;     //商品封面地址

    private String goodsTitle;          //标题

    private String basicDescribe;         //基本描述

    private String specs;                //规格

    private double price;                //价格

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            //加入时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;            //删除时间

    private int number;            //商品数量

    private int deleteType;        //删除标志：0正常，1用户删除，2管理人员删除

}
