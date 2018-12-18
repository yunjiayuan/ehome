package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 简历实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkResume {

    private long id;        //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private String educationId;        //教育经历ID:逗号分隔，格式：1,2,3,

    private String experienceId;        //工作经验ID:逗号分隔，格式：1,2,3,

    private String name;    //姓名

    private int sex;        //性别

    private String birthDay;        //出生年份

    private String positionName;        //职位名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;    //刷新时间

    private int state;        // 简历状态:0正常1已删除

    private int myJob;        // 我的求职：0我的简历  1面试邀请  2职位申请记录  3谁下载了我的简历

    private int highestEducation;    //最高学历

    private int workExperience;    //工作经验

    private int startSalary;        //期望薪资:起

    private int endSalary;        //期望薪资:始

    private int jobProvince;        // 求职区域：省

    private int jobCity;                // 求职区域：城市

    private int jobDistrict;            // 求职区域：地区或县

    private int integrity;            // 完整度

    private long downloads;        // 被下载总量

    private long browseAmount;    // 被浏览量

    private long delivery;            // 主动投递量

    private int openType;            // 公开程度：0公开  1对认证企业公开  2 只投递企业可见

    private int jobType;               // 求职类型  0全职  1兼职

    private int jobType1;            // 一级求职类型

    private int jobType2;            // 二级求职类型

    private String highlights;            // 我的亮点：逗号分隔！例：沟通力强，学习力强，责任心强

    private String selfEvaluation;    //自我评价

    private String headImgUrl; // 头像路径

    private String opusImgUrl; // 作品路径

    private int defaultResume;        // 默认简历:默认0不设置,1设置

    //与数据库无关
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private String contactsPhone;    // 联系人电话

    private String email;        //电子邮箱

    private String head;        //头像

    private int age;        //年龄

    private long houseNumber;        //门牌号

    private int proTypeId;           //	省简称ID

    private int downState;          //	下载状态：0未下载   1已下载

    private int perfectType; 		// 完善类型 0基本信息 1求职意向 2我的亮点(仅更新是有效)

}
