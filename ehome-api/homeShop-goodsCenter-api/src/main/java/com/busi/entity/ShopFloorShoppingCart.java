package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 楼店购物车
 * @author: ZHaoJiaJie
 * @create: 2019-12-09 10:48
 */
@Setter
@Getter
public class ShopFloorShoppingCart {

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

    private int number;            //商品数量

    private int deleteType;        //删除标志：0正常，1用户删除，2管理人员删除
}
