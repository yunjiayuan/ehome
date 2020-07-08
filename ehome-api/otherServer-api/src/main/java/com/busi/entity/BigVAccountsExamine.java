package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 大V审核实体
 * @author: ZhaoJiaJie
 * @create: 2020-07-07 15:09:27
 */
@Setter
@Getter
public class BigVAccountsExamine {

    private long id;

    private long userId;//用户Id

    private String idPositive;//身份证正面

    private String idBack;//身份证反面

    private String opinion;//附言

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//时间

    private int state;//状态：0审核中  1未通过  2审核通过

    //与数据库无关字段
    private String name;        //用户名称
    private String head;        //用户头像
    private int proTypeId;          //用户省简称ID
    private long houseNumber;       //用户门牌号

}
