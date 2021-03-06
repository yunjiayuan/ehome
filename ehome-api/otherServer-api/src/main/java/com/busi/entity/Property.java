package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 物业实体
 * author ZJJ
 * Create time 2020-04-07 16:46:08
 */
@Setter
@Getter
public class Property {
    private long id;//主键ID

    private long communityId;    //居委会ID

    private long userId;                // 用户ID

    private String name;                // 物业名称

    @Min(value = 0, message = "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value = 0, message = "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value = 0, message = "district参数有误，超出指定范围")
    private int district; // 地区或县

    @Digits(integer = 3, fraction = 6, message = "lat参数格式有误")
    private double lat;                    //纬度

    @Digits(integer = 3, fraction = 6, message = "lon参数格式有误")
    private double lon;                    //经度

    private String address;            // 物业地址

    private String cover;            // 封面

    private String photo;            // 物业营业执照

    private String content;            // 物业职责

    private String notice;            // 其他介绍

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            // 创建时间

    @Min(value = 0, message = "deleteType参数有误，数值超出指定范围")
    @Max(value = 2, message = "deleteType参数有误，数值超出指定范围")
    private int review;            // 审核: 0审核中 1已审核

    private long commentNumber;       //评论数

    // 与数据库无关字段
    private int distance;            //距离

    private int identity;            //身份:0普通 1管理员 2创建者

    private String tags;            //身份标签ID组  0,12,3
}
