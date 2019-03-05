package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房订单
 * @author: ZHaoJiaJie
 * @create: 2019-03-01 10:23
 */
@Setter
@Getter
public class KitchenOrders {

    private long id;                    // 主键

    @Min(value = 1, message = "myId参数有误")
    private long myId;                // 登陆者ID(买家）

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 卖家ID

    private long kitchend;                //厨房ID

    private String no;                    //订单编号

    private String dishameCost;        //菜品ID,菜名,数量,价格【格式：12,回锅肉,5,24;木须肉,4,32】

    private int ordersState;        // 订单状态:0正常 1买家删除 2商家删除 3全部删除

    private int ordersType;            //订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2制作中(已接单未发货),3配送(已发货未收货),4已卖出(已收货未评价),  5卖家取消订单 6付款超时  7接单超时 8发货超时 9用户取消订单 10 已评价

    private String kitchenName;                //厨房名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;            // 接单时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;            // 发货时间

    private String serviceTime;                // 送达时间【显示用】

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivingTime;            // 标准收货时间

    private double money;            // 商品金额

    private String smallMap;            //厨房头像【厨房封面】

    private int eatNumber;        //就餐人数

    private String remarks;        //备注

    private long addressId;            //地址ID

    private String address_Name;            // 联系人姓名

    private String address_Phone;                // 联系人电话

    private int address_province;                // 省

    private int address_city;                        // 城市

    private int address_district;                    // 地区或县

    private String address_postalcode;        // 邮政编码

    private String address;                        // 详细地址

    //与数据库无关字段
    private String goodsIds;                //菜品ID

    private String foodNumber;                //菜品数量

    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号

}
