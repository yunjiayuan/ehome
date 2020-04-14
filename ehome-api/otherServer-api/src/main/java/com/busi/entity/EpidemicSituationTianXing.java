package com.busi.entity;


import lombok.Getter;
import lombok.Setter;

/**
 * 疫情基本信息概括类（天行平台）
 * authorsuntj
 * Create time 2020/2/16 17;//04
 */
@Setter
@Getter
public class EpidemicSituationTianXing {

    private long id;//主键ID

    private long createTime;//数据创建时间

    private long modifyTime;//数据更新时间

    private String summary;

    private String deleted;

    private String countRemark;

    private int currentConfirmedCount;//现存确诊人数

    private int confirmedCount;//累计确诊人数

    private int suspectedCount;//现存疑似人数

    private int curedCount;//治愈人数

    private int deadCount;//死亡人数

    private int seriousCount;//现存重症人数

    private int suspectedIncr;//新增疑似人数

    private int currentConfirmedIncr;//相比昨天现存确诊人数

    private int confirmedIncr;//相比昨天累计确诊人数

    private int curedIncr;//相比昨天新增治愈人数

    private int deadIncr;//相比昨天新增死亡人数

    private int seriousIncr;//相比昨天现存重症人数

//    private String imgUrl;//全国疫情分布图（一张大图）
//
//    private String dailyPic;//全国疫情新增趋势图 逗号分隔 数据库字段长度给2000（该字段数据不为最新 可考虑舍弃 暂时预留）

    private String remark1;//易感人群：人群普遍易感。老年人及有基础疾病者感染后病情较重，儿童及婴幼儿也有发病,

    private String remark2;//潜伏期：一般为 3～7 天，最长不超过 14 天，潜伏期内可能存在传染性，其中无症状病例传染性非常罕见,

    private String remark3;//宿主：野生动物，可能为中华菊头蝠,

    private String remark4;//,

    private String remark5;//,

    private String note1;//病毒：SARS-CoV-2，其导致疾病命名 COVID-19,

    private String note2;//传染源：新冠肺炎的患者。无症状感染者也可能成为传染源。,

    private String note3;//传播途径：经呼吸道飞沫、接触传播是主要的传播途径。气溶胶传播和消化道等传播途径尚待明确。,

    private String generalRemark;

    private String abroadRemark;

//    private String quanguoTrendCharts;//全国疫情趋势图 格式 图片地址,标题;图片地址,标题;图片地址,标题
//
//    private String  hbFeiHbTrendCharts;//湖北/非湖北疫情趋势图 格式 图片地址,标题;图片地址,标题;图片地址,标题
//
//    private String listByArea;//全国省市地区疫情情况 json格式数据
//
//    private String listByOther;//全国省市地区疫情情况 json格式数据

}
