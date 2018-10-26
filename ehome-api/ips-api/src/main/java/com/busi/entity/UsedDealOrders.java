package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 二手订单
 * @author: ZHaoJiaJie
 * @create: 2018-10-24 13:40:08
 */
@Setter
@Getter
public class UsedDealOrders {

    private long id;                    // 主键

    private long myId;                // 登陆者ID(买家）

    private long userId;                // 卖家ID

    private long goodsId;            //商品ID

    private long logisticsId;        //物流ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;            // 付款时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;            // 发货时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivingTime;            // 标准收货时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date delayed;            // 延时后发货时间

    private String no;                    //订单编号

    private int ordersState;        // 订单状态:0正常 1买家删除 2商家删除 3全部删除

    private String title;                //商品标题

    private double money;            // 商品总金额【商品价格+邮费】

    private double sellingPrice;        //商品价格

    private int postage;                //邮费

    private int pinkageType;            //是否包邮：1否，2是

    private String picture;            //商品图

    private int distributioMode;    //  配送方式:客户端生成订单时选择快递方式的序号  -1表示到付

    private int extendFrequency;    //延长收货次数：myId一次；userId不限次数 默认0

    private int afficheType;            //公告类别标志：1婚恋交友,2二手交易,3寻人,4寻物,5失物招领,6其他（注：后续添加）

    private int ordersType;            // 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时 7发货超时

    private long addressId;            //地址ID

    private String address_Name;            // 联系人姓名

    private String address_Phone;                // 联系人电话

    private int address_province;                // 省

    private int address_city;                        // 城市

    private int address_district;                    // 地区或县

    private String address_postalcode;        // 邮	政编码

    private String address;                        // 详细地址

    private String name;                //用户名	查询后从内存获取最新  与数据库无关字段

    private String head;                    //头像	查询后从内存获取最新  与数据库无关字段

    private int proTypeId;                //	省简称ID  与数据库无关字段

    private long houseNumber;        // 门牌号  与数据库无关字段

}
