package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 评选活动
 * @author: ZHaoJiaJie
 * @create: 2018-10-10 12:01:02
 */
@Setter
@Getter
public class SelectionActivities {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//用户ID

    private int selectionType;//评选类型 0云家园招募令  1城市小姐  2校花  3城市之星  4青年创业

    private int s_province;//省ID

    private int s_city;//市ID

    private int s_district;//区ID

    private String s_name;//名字

    private int s_sex;//性别 1男2女

    private String s_birthday;//出生日期

    private int s_job;//selectionType=1时为职业 "0":"请选择","1":"在校学生","2":"计算机/互联网/IT","3":"电子/半导体/仪表仪器","4":"通讯技术","5":"销售","6":"市场拓展","7":"公关/商务","8":"采购/贸易","9":"客户服务/技术支持","10":"人力资源/行政/后勤","11":"高级管理","12":"生产/加工/制造","13":"质检/安检","14":"工程机械","15":"技工","16":"财会/审计/统计","17":"金融/证券/投资/保险","18":"房地产/装修/物业","19":"仓储/物流","20":"交通/运输","21":"普通劳动力/家政服务","22":"普通服务行业","23":"航空服务业","24":"教育/培训","25":"咨询/顾问","26":"学术/科研","27":"法律","28":"设计/创意","29":"文学/传媒/影视","30":"餐饮/旅游","31":"化工","32":"能源/地址勘察","33":"医疗/护理","34":"保健/美容","35":"生物/制药/医疗机械","36":"体育工作者","37":"翻译","38":"公务员/国家干部","39":"私营业主","40":"农/林/牧/渔业","41":"警察/其他","42":"自由职业者","43":"其他"
    //selectionType=2时为学校名称：

    private int s_maritalStatus;//selectionType=2时为婚否 0:"请选择",1:"已婚",2:"未婚",3:"离异",4:"丧偶"
    //selectionType=2时为血型：0：A型 ，1：B型，2：AB型，3：O型

    private int s_height;//身高

    private int s_weight;//体重

    private String s_introduce;//个人介绍

    private String imgUrl; //图片

    private long votesCounts;//票数

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//参加时间

    private int status;//状态 0正常 1封贴

    private String activityVideo;//视频base64

    private String activityCover;//活动封面

    private int auditType;      // 审核标志:0待审核,1通过

    private String spokesmanName;//代言人名称 例如：北京海淀代言人

    //与数据库无关字段
    private String delUrls;//将要删除的地址组合 “,”分隔

    private String name; //用户名

    private String head; //头像

    private int proTypeId;//省简称ID

    private long houseNumber;//门牌号

    private int realNameStatus;//实名状态 ：默认0未认证  1已认证


}
