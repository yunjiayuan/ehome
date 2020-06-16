package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房订座数据实体
 * @author: ZhaoJiaJie
 * @create: 2020-06-15 12:33:34
 */
@Setter
@Getter
public class KitchenReserveData {

    private long id;                    // 主键ID

    private String uid;                    // 唯一标识符

    private String streetID;                    // 对应街景图ID

    private long userId;                // 商家ID

    private int claimStatus;        // 认领状态:0待认领 1已认领

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date claimTime;        // 认领时间

    private String province;//所在省份

    private String city;//所在城市

    private String area;//所在行政区域

    @Length(max = 14, message = "厨房名称不能超过14字")
    private String name;                //厨房名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private double latitude;                    //纬度

    private double longitude;                    //经度

    @Length(max = 46, message = "详细地址不能超过46字")
    private String address;            // 详细地址

    private String phone;//店主电话

    private int distance;//距离中心点的距离

    private String type;//类型

    private String tag;//标签

    private String detailURL;//详情页URL

    private double price;//商户的价格

    private String openingHours;//营业时间

    private long overallRating;//总体评分

    private double tasteRating;//口味评分

    private double serviceRating;//服务评分

    private double environmentRating;//环境评分

    private double hygieneRating;//卫生评分

    private double technologyRating;//技术评分

    private double facilityRating;//星级（设备）评分

    private int imageNumber;//图片数目

    private int grouponNumber;//团购数目

    private int discountNumber;//优惠数目

    private int commentNumber;//评论数目

    private int favoriteNumber;//收藏数目

    private int checkInNumber;//签到数目

    //与数据库无关字段
    private int range;            //距离
}
