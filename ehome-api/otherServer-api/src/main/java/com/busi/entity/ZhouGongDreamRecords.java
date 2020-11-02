package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 周公解梦历史记录实体
 * @author: ZhaoJiaJie
 * @create: 2020-10-30 15:06:11
 */
@Setter
@Getter
public class ZhouGongDreamRecords {

    private long id;    //ID

    private long userId;//用户Id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;    //解梦时间

    private long dreamId;//梦ID

    private String title;//标题
}
