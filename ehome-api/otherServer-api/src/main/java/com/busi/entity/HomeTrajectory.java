package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 居家轨迹（疫情）
 * author ZJJ
 * Create time 2020-02-16 12:15:41
 */
@Setter
@Getter
public class HomeTrajectory {

    private long id;                       //主键ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;                   //用户ID

    private String trajectory;                   //轨迹(逗号分隔)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                     //新增时间
}
