package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @program: ehome
 * @description: 家医馆实体
 * @author: ZHaoJiaJie
 * @create: 2020-01-07 10:52
 */
@Setter
@Getter
public class HomeHospital {

    private long id;                    // 医馆ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;				// 医师ID

    @Min(value = 0, message = "businessStatus参数有误，数值超出指定范围")
    @Max(value = 0, message = "businessStatus参数有误，数值超出指定范围")
    private int businessStatus; 		// 营业状态:0正常 1暂停

    @Min(value = 0, message = "deleteType参数有误，数值超出指定范围")
    @Max(value = 2, message = "deleteType参数有误，数值超出指定范围")
    private int deleteType;				 // 删除标志:0未删除,1用户删除,2管理员删除

    @Min(value = 0, message = "auditType参数有误，数值超出指定范围")
    @Max(value = 2, message = "auditType参数有误，数值超出指定范围")
    private int auditType; 			// 审核标志:0审核中,1通过,2未通过

    private int title; 			// 职称

    private int department; 	// 科室

    @Length(max = 6, message = "医师姓名不能超过6字")
    private String physicianName;				//医师姓名

    @NotEmpty(message ="医院不能为空")
    private String hospital;				//医院

    @NotEmpty(message ="擅长不能为空")
    @Length(max = 300, message = "擅长不能超过300字")
    private String major;			    // 擅长

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;			// 添加时间

    private String imgUrl;				//资格证

    @NotEmpty(message ="头像不能为空")
    private String headCover;		//头像

    @NotEmpty(message ="简介不能为空")
    private String content;				//简介

    @Min(value = 0, message = "jobStatus参数有误，数值超出指定范围")
    @Max(value = 1, message = "jobStatus参数有误，数值超出指定范围")
    private int jobStatus;				//在职状态：  0正常 1退休

    @Min(value= 0 ,message= "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value= 0 ,message= "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value= 0 ,message= "district参数有误，超出指定范围")
    private int district; // 地区或县

    private long practiceNumber; 	// 执业编号

    private long helpNumber; 	// 帮助人数

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    //与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private int age;     	 //年龄

    private int sex; 		// 性别:1男,2女

    private String name; 			    //用户名	查询后从内存获取最新

    private String head; 			   		//头像	查询后从内存获取最新

    private int proTypeId;	 			//	省简称ID

    private long houseNumber;		// 门牌号

}
