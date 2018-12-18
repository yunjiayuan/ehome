package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 面试通知实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkInterview {

    private long id;        //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //发布者ID

    private long companyId;        //企业ID

    private long resumeId;        //简历ID

    private long notifiedUserId;        //被通知者用户ID

    private String corporateName;    //公司名称

    private String positionName;        //职位名称

    private String address;        //详细地址

    private String contactPeople;    //联系人

    private String contactsPhone;    // 联系人电话

    private String interviewTime;    //面试时间

    private String remarks;    //备注

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    private int deleteState;        // 删除状态:0正常1已删除

}
