package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 工作经验实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkExperience {

    private long id;        //主键

    private long resumeId;        //简历ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private String companyName;    //公司名称

    private String positionName; // 职位名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    private String startTime;    //开始时间

    private String endTime;    //结束时间

    private String content; // 工作内容

    private int state;        // 状态:0正常1已删除

}
