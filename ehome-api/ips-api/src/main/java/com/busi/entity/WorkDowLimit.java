package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 下载简历限制实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkDowLimit {

    private long id;        //ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                //用户id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastDowDate;            //记录用户最后一次下载简历日期 (判断是否同一天)

    private int dowResumeTimes;            //记录用户今日下载简历剩余次数

}
