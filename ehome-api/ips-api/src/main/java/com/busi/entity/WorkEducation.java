package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 教育经历实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkEducation {

    private long id;        //主键

    private long resumeId;        //简历ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private int schoolName;    //学校名称

    private int proTypeId;//省简称ID

    private String majorName; // 专业名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    private String graduationTime;    //毕业时间

    private int state;        // 状态:0正常1已删除
}
