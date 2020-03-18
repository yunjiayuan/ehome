package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 社区
 * author ZJJ
 * Create time 2020-03-16 17:16:06
 */
@Setter
@Getter
public class Community {
    private long id;//主键ID

    private long userId;                // 用户ID

    private String name;                // 居委会名称

    private double lat;                    //纬度

    private double lon;                    //经度

    private String address;            // 详细地址

    private String cover;            // 封面

    private String iDPhoto;            // 证件照

    private String content;            // 简介

    private String notice;            // 公告

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            // 创建时间

    private int review;            // 审核: 0审核中 1已审核

    // 与数据库无关字段
    private int distance;            //距离
}
