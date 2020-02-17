package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 疫情基本信息概括类（天气平台）
 * authorsuntj
 * Create time 2020/2/16 17;//04
 */
@Setter
@Getter
public class EpidemicSituationTianqi {

    private long id;//主键ID

    private String date;//更新时间

    private int diagnosed;//确诊人数

    private int  suspect;//疑似人数

    private int  death;//死亡人数

    private int  cured;//治愈人数

    private int  serious;//重症人数

    private int  diagnosedIncr;//新增确诊人数

    private int  suspectIncr;//新增疑似人数

    private int  deathIncr;//新增死亡人数

    private int  curedIncr;//新增治愈人数

    private int  seriousIncr;//新增重症人数

    private String list;//Array[34],各省数据列表

    private String history;//Array[24],历史数据

    private String area;//Array[34] 各省市数据列表

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//入库时间

}
