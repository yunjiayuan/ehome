package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 租房买房订单实体
 * @author: ZhaoJiaJie
 * @create: 2021-03-29 16:38:53
 */
@Setter
@Getter
public class RentAhouseOrder {

    private long id;                    // 主键

    private int roomState;             //0购房 1租房

    private long myId;                // 租户或购房者ID

    private long userId;                // 房主ID

    private long houseId;          // 房屋ID

    private String no;                    //订单编号

    private String villageName;       // 小区名称

    private String picture;    //图片

    private int province; // 省

    private int city; // 城市

    private int district;       //区

    private String houseNumber;          //楼栋编号

    private int houseCompany;         //单位：0号楼、1栋、2幢、3座、4无

    private String unitNumber;             //单元编号

    private int unitCompany;           //单位：0单元、1无单元

    private String roomNumber;             //室 具体房间号

    private int residence;              //户型 几居室

    private int livingRoom;             //户型 几厅

    private int toilet;                 //户型 几卫

    private int roomType;             //房屋类型 roomState=0时：0新房 1二手房   roomState=1时：0合租 1整租

    private int bedroomType;             //卧室类型 0主卧 1次卧 2其他

    private int leaseContract;        //租房协议 0未签署  1已签署

    private double deposit;             //押金

    private double money;             //roomState=0时为售价  roomState=1时为 月/租金

    private double price;             //本次支付总金额

    private int paymentMethod;       //支付方式 0押一付一  1押一付三  2押一半年付  3押一年付

    private int paymentStatus;           //支付状态  0未付款 1已付款  2已过期

    private int makeMoneyStatus;           //给房主打款状态  0未打款 1已打款

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 下单时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 首次支付时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextPaymentTime;            // 下次支付时间

    private int duration;            //已累计支付房租时长

    private double rentMoney;            //已累计支付房租金额

    private int ordersState;        // 订单状态:0正常 1买家删除 2房主删除 3全部删除

    private int renewalState;        // 续租状态:0未续租  1已续租

    private String telephone;        // 电话

    private int housingArea;         //房屋面积

    private int orientation;         //房屋朝向 0南北、1东北、2东南、3西南、4西北、5东西、6南、7北、8东、9西
}
