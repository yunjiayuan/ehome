package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 家门口商家实体
 * @author: ZhaoJiaJie
 * @create: 2020-11-10 16:15:05
 */
@Setter
@Getter
public class DoorwayBusiness {

    private long id;                    // 商家ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 商家ID

    private int businessStatus;        // 营业状态:0正常 1打烊

    private int deleteType;                 // 删除标志:0未删除,1用户删除,2管理员删除

    private int auditType;            // 审核标志:0审核中,1通过,2未通过

    @Length(max = 14, message = "商家名称不能超过14字")
    private String businessName;                //商家名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private String licence;        //营业执照

    private String picture;        //图片

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    @Length(max = 300, message = "简介不能超过300字")
    private String content;                //简介

    private long totalSales;        // 总销量

    private long totalScore;        // 总评分

    private int averageScore;        // 平均评分

    private double lat;                    //纬度

    private double lon;                    //经度

    @Min(value = 0, message = "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value = 0, message = "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value = 0, message = "district参数有误，超出指定范围")
    private int district; // 地区或县

    @Length(max = 46, message = "详细地址不能超过46字")
    private String address;            // 详细地址

    private String phone;//电话

    private int type;            // 商家类型

    private int openType;                // 开放类型:0全天 1时间段

    private String openTime;                // 开放时间 openType=1时有效

    private String closeTime;                // 关闭时间 openType=1时有效

    @Length(max = 140, message = "贴士不能超过140字")
    private String tips;                //贴士

    private double cost;       //商品最低价格

    private int free;       //配送方式：0免费 1满额

    private int freeCost;       //配送价格

    //与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private int distance;            //距离

    private String name;                //用户名	查询后从内存获取最新

    private String head;                    //头像	查询后从内存获取最新

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号
}
