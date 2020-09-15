package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 抽签实体
 * @author: ZhaoJiaJie
 * @create: 2020-09-14 19:55:52
 */
@Setter
@Getter
public class Drawings {

    private long id;    //ID

    private int signNum;//签号

    private String sign;//中文签号

    private String allusionName;//典故名称

    private String goodOrBad;//吉凶宫位

    private String poeticFlavour;//诗意

    private String heSaid;//解曰

    private String immortalMachine;//仙机

    private String whole;//整体解译

    private String quintessence;//本签精髓

    private String allusion;//典故

    private String cause;//工作求职/创业事业

    private String business;//经商生意

    private String investment;//投资理财

    private String love;//爱情婚姻

    private String work;//凡事做事

    private String travelFar;//远行出国

    private String seek;//寻人寻物

    private String lawsuit;//官司诉讼

    private String prayForAson;//求孕求子

    private String examination;//考试竞赛/升迁竞选

    private String transaction;//房地交易

    private String changes;//转换变更

    private String healthy;//治病健康

}
