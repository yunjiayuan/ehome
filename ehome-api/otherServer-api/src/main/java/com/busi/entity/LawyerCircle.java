package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @program: ehome
 * @description: 律师圈实体
 * @author: ZHaoJiaJie
 * @create: 2020-03-03 15:53:44
 */
@Setter
@Getter
public class LawyerCircle {

    private long id;                    // ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 律师ID

    @Min(value = 0, message = "businessStatus参数有误，数值超出指定范围")
    @Max(value = 0, message = "businessStatus参数有误，数值超出指定范围")
    private int businessStatus;        // 营业状态:0正常 1暂停

    @Min(value = 0, message = "deleteType参数有误，数值超出指定范围")
    @Max(value = 2, message = "deleteType参数有误，数值超出指定范围")
    private int deleteType;                 // 删除标志:0未删除,1用户删除,2管理员删除

    @Min(value = 0, message = "auditType参数有误，数值超出指定范围")
    @Max(value = 2, message = "auditType参数有误，数值超出指定范围")
    private int auditType;            // 审核标志:0审核中,1通过,2未通过

    private int title;            // 职称(初级律师、中级律师、高级律师)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private String imgUrl;                //资格证

    @NotEmpty(message = "头像不能为空")
    private String headCover;        //头像

    @NotEmpty(message = "简介不能为空")
    private String content;                //简介

    @Min(value = 0, message = "jobStatus参数有误，数值超出指定范围")
    @Max(value = 1, message = "jobStatus参数有误，数值超出指定范围")
    private int jobStatus;                //在职状态：  0正常 1退休

    @Min(value = 0, message = "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value = 0, message = "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value = 0, message = "district参数有误，超出指定范围")
    private int district; // 地区或县

    private long lvshiNumber;    // 律师职业资格证编号（17位）

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    @Digits(integer = 3, fraction = 6, message = "longitude参数格式有误")
    private double longitude;//东经

    @Digits(integer = 3, fraction = 6, message = "latitude参数格式有误")
    private double latitude;//北纬

    private int cityId;//百度地图中的城市ID，用于同城搜索

    @Length(max = 6, message = "律师姓名不能超过6字")
    private String lvshiName;                //律师姓名

    private int lvshiType;        // 律师类型（专职律师0、兼职律师1、香港居民律师2、澳门居民律师3、台湾居民律师4、公职律师5、公司律师6、法律援助律师7、军队律师8、其他律师9）

    private String beGoodAt;        // 擅长案件描述

    private String lawFirm;        // 所在律所名称

    private long helpNumber; 	// 帮助人数

    //与数据库无关字段
    private int age;         //年龄

    private int sex;        // 性别:0男,1女

    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private String name; 			    //用户名	查询后从内存获取最新

    private String head; 			   		//头像	查询后从内存获取最新

    private int proTypeId;	 			//	省简称ID

    private long houseNumber;		// 门牌号

}
