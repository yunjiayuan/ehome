package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 景区订单实体
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 11:14:15
 */
@Setter
@Getter
public class ScenicSpotOrder {

    private long id;                    // 主键

    private long myId;                // 买家ID

    private long userId;                // 商家ID

    private long scenicSpotId;          // 景区ID

    private String no;                    //订单编号

    private String dishameCost;        //门票  门票ID,名称,数量,价格【格式：12,通票,5,24;儿童票,4,32】

    private int ordersState;        // 订单状态:0正常 1买家删除 2商家删除 3全部删除

    private int ordersType;            //订单类型:0未验票 1已验票,2已完成（未评价）,3已评价，4商家取消订单,5用户取消订单 ,6已过期

    private String scenicSpotName;                //景区名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 下单时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 付款时间

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date playTime;            // 游玩日期

    private int paymentStatus;           //支付状态  0未付款 1已付款

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date inspectTicketTime;        //验票时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;            // 完成时间

    private double money;            // 门票金额

    private String smallMap;            //景区头像

    private int playNumber;        //游玩人数

    private String address_Name;            // 联系人姓名

    private String address_Phone;                // 联系人电话

    private String voucherCode;                // 凭证码

    //与数据库无关字段
    private String ticketsIds;             //门票IDs

    private String ticketsNumber;           //门票数量

    private String name;                //用户名

    private String head;                 //头像

    private int proTypeId;              //	省简称ID

    private long houseNumber;        // 门牌号

    private long updateCategory;        // 更新类别  0删除状态  1由未验票改为已验票  2由已验票改为已完成  3更新支付状态  4取消订单、评价状态

}
