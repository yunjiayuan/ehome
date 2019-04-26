package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 小时工订单
 * @author: ZHaoJiaJie
 * @create: 2019-1-7 15:23:55
 */
@Setter
@Getter
public class HourlyWorkerOrders {

    private long id;                    // 主键

    private long myId;                // 客户ID(下单者）

    private long userId;                // 小时工ID

    private long shopId;            //店铺ID

    private String no;                    //订单编号

    private int ordersState;        // 订单状态:0正常 1买家删除 2商家删除 3全部删除

    private int ordersType;            //订单类型:  0已下单未付款  1已接单未完成  ,2已完成(已完成未评价),  3接单超时  4商家取消订单 5用户取消订单  6已评价 7付款超时

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 付款时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;            // 接单时间

    private String serviceTime;                // 到达时间【显示用】

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivingTime;            // 完成时间

    private String workerTypeIds;        //ID&工作类型【格式：12,打扫卫生;2,擦桌子;】

    private String remarks;        //备注

    private double money;            // 佣金

    private long addressId;            //地址ID

    private String address_Name;            // 联系人姓名

    private String address_Phone;                // 联系人电话

    private int address_province;                // 省

    private int address_city;                        // 城市

    private int address_district;                    // 地区或县

    private String address_postalcode;        // 邮	政编码

    private String address;                        // 详细地址

    private String coverMap;            //小时工头像

    private String name;                //小时工姓名

    //与数据库无关字段

    private int proTypeId;                //	省简称ID  与数据库无关字段

    private long houseNumber;        // 门牌号  与数据库无关字段

    private long updateCategory;        // 更新类别  默认0删除状态  1由未接单改为已接单  2由服务中改为已完成  3取消订单  4更新订单状态为已评价  5更新支付状态

}
