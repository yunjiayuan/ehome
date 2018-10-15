package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: 黄历实体类
 * @description:
 * @author: ZHaoJiaJie
 * @create: 2018-10-10 15:40:18
 */
@Setter
@Getter
public class NotepadLunar {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gregorianDatetime;        //阳历日期

    private String lunarDatetime;    //阴历日期

    private String yi;    //宜

    private String ji;    //忌

    private String taiShen;        //胎神

    private String chong;    //冲

    private String wuXingJiaZi;    //五行甲子

    private String wuXingNaYear;    //五行甲子年

    private String wuXingNaMonth;    //五行甲子月

    private String wuXingNaDay;        //五行甲子日

    private String moonName;        //月相

    private String xingEast;    //28星宿

    private String pengZu;    //彭祖百忌

    private String tianGanDiZhiYear;    //天干地支年

    private String tianGanDiZhiMonth;        //天干地支月

    private String tianGanDiZhiDay;    //天干地支日

    private String lYear;    //生肖

    private String lMonth;    //阴历月

    private String lDay;  //阴历天

}
