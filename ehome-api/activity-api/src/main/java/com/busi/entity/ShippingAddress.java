package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description:抽奖收货地址
 * @author: ZHaoJiaJie
 * @create: 2018-09-14 10:29
 */
@Setter
@Getter
public class ShippingAddress {

    private long id;                //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;			//用户

    private String contactsName; 	// 联系人姓名

    private String contactsPhone; 	// 联系人电话

    private int province; 			// 省

    private int city; 				// 城市

    private int district; 			// 地区或县

    private String postalcode; 		// 邮政编码

    private String address; 		// 详细地址

    private int addressState; 		// 地址状态:0正常1已删除

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;			// 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;		// 更新时间

    private int defaultAddress;		// 默认地址:默认0不设置,1设置
}
