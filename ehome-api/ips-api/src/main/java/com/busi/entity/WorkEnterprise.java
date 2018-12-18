package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 企业信息实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkEnterprise {

    private long id;        //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private String corporateName;        //公司名称

    private int industry;        //所属行业

    private int companySize;    //公司规模

    private int companyNature;        //公司性质

    private String companypProfile;            // 公司简介

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    private int state;        // 状态:0正常1已删除

    private int jobProvince;        // 工作地址：省

    private int jobCity;                // 工作地址：城市

    private int jobDistrict;            // 工作地址：地区或县

    private String imgUrl;            //图片

    private long downloads;        // 下载简历总量

}
