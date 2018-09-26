package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 收货信息
 * @author: ZHaoJiaJie
 * @create: 2018-09-14 11:11
 */
@Setter
@Getter
public class PrizesReceipt {

    private long id;                //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;			//用户

    private String costName;	//奖品名称

    private String describes;	//奖品描述

    private int issue;		//期号

    private int price;//奖品价值

    private String imgUrl;			//奖品图

    private String contactsName; 	// 联系人姓名

    private String contactsPhone; 	// 联系人电话

    private int province; 			// 省

    private int city; 				// 城市

    private int district; 			// 地区或县

    private String postalcode; 		// 邮政编码

    private String address; 		// 详细地址

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;			// 添加时间
}
