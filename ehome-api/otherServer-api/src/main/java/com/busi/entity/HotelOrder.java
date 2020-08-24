package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 酒店民宿订单实体
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 14:52:49
 */
@Setter
@Getter
public class HotelOrder {

    private long id;                    // 主键

    private long myId;                // 买家ID

    private long userId;                // 商家ID

    private long hotelId;          // 酒店&民宿&景区ID

    private int hotelType;          //订单所属类别： 0酒店 1民宿  2景区

    private String no;                    //订单编号

    private String dishameCost;        //房间  房间ID,名称,数量,价格【格式：12,大床房,5,24;标间,4,32】

    private int ordersState;        // 订单状态:0正常 1买家删除 2商家删除 3全部删除

    private int ordersType;            //订单类型:0未入住（验票） 1已入住（验票）,2已完成（未评价）,3已评价，4商家取消订单,5用户取消订单 ,6已过期

    private String hotelName;                //酒店&民宿名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 下单时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 付款时间

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date checkInTime;            // 入住日期

    private int paymentStatus;           //支付状态  0未付款 1已付款

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date inspectTicketTime;        //验票时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;            // 完成时间

    private double money;            // 房间价格

    private String smallMap;            //酒店&民宿封面

    private int checkInNumber;        //入住人数

    private String address_Name;            // 联系人姓名

    private String address_Phone;                // 联系人电话

    private String voucherCode;                // 凭证码

    //与数据库无关字段
    private String ticketsIds;             //房间IDs

    private String ticketsNumber;           //房间数量

    private String name;                //用户名

    private String head;                 //头像

    private int proTypeId;              //	省简称ID

    private long houseNumber;        // 门牌号

    private long updateCategory;        // 更新类别  0删除状态  1由未入住（验票）改为已入住（验票）  2由已入住（验票）改为已完成  3更新支付状态  4取消订单、评价状态

}
