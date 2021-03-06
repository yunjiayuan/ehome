package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 楼店店主订单实体
 * @author: ZHaoJiaJie
 * @create: 2020-01-09 15:24
 */
@Setter
@Getter
public class ShopFloorMasterOrders {

    private long id;                    // 主键

    private long buyerId;                // 买家ID

    private long shopId;                //店铺ID

    private String shopName;            //店铺名称

    private String goods;            //分类ID,商品ID,标题,数量,价格,图片,规格,商品描述【格式：11/5/1,12,啤酒,5,9,图片,规格,好吃不贵;11/5/1,22,啤酒,7,19,图片,规格,好吃不贵】

    private double money;            // 商品总金额

    private int distributioMode;    //  配送方式

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 付款时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;            // 发货时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivingTime;            // 收货时间

    private String no;            //订单编号

    private int ordersState;        // 订单状态:0正常 1删除

    private int ordersType;            // 订单类型: 0待付款,1待发货(已付款),2已发货（待收货）, 3已收货（待评价）  4已评价  5付款超时  6发货超时, 7取消订单

    private long addressId;            //地址ID

    private String address;             // 详细地址

    private String addressName;        // 联系人姓名

    private String addressPhone;       // 联系人电话

    private int addressProvince;      // 省

    private int addressCity;          // 城市

    private int addressDistrict;      // 地区或县

    private String remarks;            //买家留言

    //与数据库无关字段
    private String name;                //用户名

    private String head;                 //头像

    private int proTypeId;              //省简称ID

    private long houseNumber;        // 门牌号

    private String goodsIds;             //商品ID：逗号分隔

    private String goodsNumber;           //商品数量：逗号分隔

    private long updateCategory;        // 更新类别  0删除状态  1更新支付状态  2发货  3收货  4取消订单、评价状态

}
