package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 抽签记录
 * @author: ZhaoJiaJie
 * @create: 2020-09-14 19:55:52
 */
@Setter
@Getter
public class DrawingRecords {

    private long id;    //ID

    private long userId;//用户Id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;    //抽签时间

    private long drawingId;//签子ID

    private String signature;//名称

    //与数据库无关字段
//    private String name; //用户名
//
//    private String head; //头像
//
//    private int proTypeId;     //	省简称ID
//
//    private long houseNumber;// 门牌号
}
