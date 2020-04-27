package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 租房买房实体
 * author ZhaoJiaJie
 * Create time 2020-04-20 11:34:02
 */
@Setter
@Getter
public class RentAhouse {

    private long id;                //主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;            //用户ID

    private int roomState;             //房屋状态：0出售 1出租

    private int sellState;             //roomState=0时：0出售中  1已售出  roomState=1时：0出租中  1已出租

    @NotEmpty(message = "标题不能为空")
    private String title;    //标题

    private String formulation;    //描述

    @NotEmpty(message = "图片不能为空")
    private String picture;    //图片

    private String videoUrl;    //视频地址

    private String videoCover;    //视频封面

    @NotEmpty(message = "小区名称不能为空")
    private String villageName;       // 小区名称

    @Min(value = 0, message = "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value = 0, message = "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value = 0, message = "district参数有误，超出指定范围")
    private int district;       //区

    @Digits(integer = 3, fraction = 6, message = "lat参数格式有误")
    private double lat;                    //纬度

    @Digits(integer = 3, fraction = 6, message = "lon参数格式有误")
    private double lon;                    //经度

    @NotEmpty(message = "小区名称不能为空")
    private String houseNumber;          //楼栋编号

    private int houseCompany;         //单位：0号楼、1栋、2幢、3座、4无

    @NotEmpty(message = "单元编号不能为空")
    private String unitNumber;             //单元编号

    private int unitCompany;           //单位：0单元、1无单元

    @NotEmpty(message = "具体房间号不能为空")
    private String roomNumber;             //室 具体房间号

    private int residence;              //户型 几居室

    private int livingRoom;             //户型 几厅

    private int toilet;                 //户型 几卫

    private int housingArea;         //户型面积

    private int roomType;             //房屋类型 roomState=0时：0新房 1二手房   roomState=1时：0合租 1整租

    private int orientation;             //房屋朝向 0南北、1东北、2东南、3西南、4西北、5东西、6南、7北、8东、9西

    private int renovation;             //房屋装修 0精装修  1普通装修  2毛坯房

    private int rentalType;             //公告分数

    private int bedroomType;             //卧室类型 0主卧 1次卧 2其他

    private int houseType;             //房源类型 0业主直租 1中介

    private int expectedPrice;             //roomState=0时为期望售价  roomState=1时为期望租金

    private int paymentMethod;             //支付方式 0押一付一 1押一付三 2季付 3半年付 4年付

    private int lookHomeTime;             //看房时间  0随时看房 1 周末看房  2下班后看房  3电话预约

    @NotEmpty(message = "业主姓名不能为空")
    private String realName;        //业主姓名

    private int elevator;       //电梯 0没有 1有

    private int propertyFee;        //物业费 0没有 1有

    private int heatingCost;        //取暖费 0没有 1有

    private int floor;        //楼层：0底层 1低楼层 2中楼层 3高楼层 4顶层

    private int totalFloor;        //总楼层

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 新增时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;            // 刷新时间

    private int state;        // 状态:0正常1已删除

}
