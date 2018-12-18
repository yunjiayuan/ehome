package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @program: 工作简历的完整性实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkResumeIntegrity {

    private long id;            //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private long resumeId;        //简历ID

    private int workEducation;         //教育经历   完整度   0待完善   1完整

    private int workExperience;       //工作经验  完整度   0待完善   1完整

    private int highlights;                //我的亮点  完整度   0待完善   1完整

    private int photo;                //照片   完整度   0待完善   1完整

}
