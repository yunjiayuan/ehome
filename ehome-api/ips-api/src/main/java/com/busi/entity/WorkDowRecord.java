package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 简历下载记录实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkDowRecord {

    private long id;        //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //下载者ID

    private long companyId;        //企业ID

    private long resumeUserId;        //被下载简历用户ID

    private long resumeId;        //被下载简历ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    private String name;    //姓名

    private int sex;        //性别

    private int highestEducation;    //最高学历

    private int workExperience;    //工作经验

    private String highlights;            // 我的亮点：逗号分隔！例：沟通力强，学习力强，责任心强

    private int jobType2;            // 二级求职类型

    private int jobProvince;        // 求职区域：省

    private String corporateName;    //公司名称

    private String head;        //简历头像
    //与数据库无关字段
//    private int age;        //年龄
//
//    private long houseNumber;        //门牌号	查询后从内存获取最新
//
//    private int proTypeId;                //	省简称ID

}
