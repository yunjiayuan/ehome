package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房
 * @author: ZHaoJiaJie
 * @create: 2019-03-01 10:13
 */
@Setter
@Getter
public class Kitchen {

    private long id;                    // 厨房ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 商家ID

    private int businessStatus;        // 营业状态:0正常 1暂停

    private int deleteType;                 // 删除标志:0未删除,1用户删除,2管理员删除

    private int auditType;            // 审核标志:0审核中,1通过,2未通过

    private String cuisine;            //菜系 [格式：川菜，粤菜（逗号分隔）]  菜系内容：鲁菜、川菜、粤菜、苏菜、闽菜、浙菜、湘菜、徽菜、其他

    @Length(max = 13, message = "拿手菜不能超过13字")
    private String goodFood;            //拿手菜

    @Length(max = 14, message = "厨房名称不能超过14字")
    private String kitchenName;                //厨房名称

    private int startingTime;                // 起送时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private String healthyCard;        //健康证

    private String kitchenCover;        //封面

    @Length(max = 300, message = "简介不能超过300字")
    private String content;                //简介

    private long totalSales;        // 总销量

    private long totalScore;        // 总评分

    private double lat;                    //纬度

    private double lon;                    //经度

    @Length(max = 46, message = "详细地址不能超过46字")
    private String address;            // 详细地址

    //与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private int age;         //年龄

    private int sex;        // 性别:1男,2女

    private int distance;            //距离

    private String name;                //用户名	查询后从内存获取最新

    private String head;                    //头像	查询后从内存获取最新

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号

}
