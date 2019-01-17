package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 小时工工作类型
 * @author: ZHaoJiaJie
 * @create: 2019-1-7 15:23:55
 */
@Setter
@Getter
public class HourlyWorkerType {

    private long id;                    // ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 小时工ID

    private long workerId;            // 店铺ID

    private String charge;        // 收费标准

    private String workerType;    // 工作类型

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private long pointNumber;            //点赞数

    private long sales;                    //服务量
}
