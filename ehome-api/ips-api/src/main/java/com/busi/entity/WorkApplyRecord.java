package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 职位申请记录实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkApplyRecord {

    private long id;        //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //申请者ID

    private long resumeId;        //投递简历ID

    private long recruitId;        //招聘信息ID

    private long companyId;        //企业注册者用户ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;    //刷新时间

    private int state;        // 申请记录状态:0正常1已删除

    private int employmentStatus;        // 录用状态:默认0不限 1通知面试 2录用  3不合格

    private int enterpriseFeedback;        // 企业反馈:默认0不限 1企业已查看 2感兴趣  3待反馈

    private int dowtype;        // 记录类别：0主动申请   1企业下载

}
