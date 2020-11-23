package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 家门口隐形商家订单实体
 * @author: ZhaoJiaJie
 * @create: 2020-08-10 14:18:32
 */
@Setter
@Getter
public class DoorwayBusinessOrder {

    private long id;                    // 主键

    private long myId;                // 买家ID

    private long userId;                // 商家ID

    private long pharmacyId;          // 商家ID

    private String no;                    //订单编号

    private String dishameCost;        //商品：  商品ID,名称,数量,价格,图片【格式：12,儿童药,5,24,图片,规格;儿童药,4,32,图片】

    private int ordersState;        // 订单状态:0正常 1买家删除 2商家删除 3全部删除

    private int verificationType;            //订单状态: 0待验证 1已验证 2已评价 3卖家取消订单 4用户取消订单 5付款超时

    private int ordersType;            // 【此字段暂时无效！！！】  订单类型: 0未接单,1待配送 2配送中,3已送达(已收货未评价), 4卖家取消订单 5用户取消订单 6付款超时  7接单超时 8发货超时 9已评价 10收货超时

    private String pharmacyName;                //商家名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 下单时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 付款时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;            // 接单时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;            // 发货时间

    private String serviceTime;                // 期望配送时间

    private int paymentStatus;           //支付状态  0未付款 1已付款

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date inspectTicketTime;        //验证时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;            // 完成时间

    private double money;            // 商品金额

    private String smallMap;            //商家头像

    private long addressId;            //地址ID

    private String address_Name;            // 联系人姓名

    private String address_Phone;                // 联系人电话

    private String address;                        // 详细地址

    private String voucherCode;                // 凭证码

    private int distributionMode;            // 配送方式 0商家配送 1买家自取

    //与数据库无关字段
    private String ticketsIds;             //商品IDs

    private String ticketsNumber;           //商品数量

    private String name;                //用户名

    private String head;                 //头像

    private int proTypeId;              //	省简称ID

    private long houseNumber;        // 门牌号

    private long updateCategory;        // 更新类别  0删除状态  1待配送  2配送中  3已送达 4更新支付状态  5取消订单、评价状态  6验证状态
}
