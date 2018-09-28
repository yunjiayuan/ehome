package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 新人分享红包信息实体类
 * @author: ZHaoJiaJie
 * @create: 2018-09-27 15:45
 */
@Setter
@Getter
public class ShareRedPacketsInfo {

    private long id;//主键ID

    @Min(value = 1, message = "shareUserId参数有误")
    private long shareUserId;//分享者用户ID

    @Min(value = 1, message = "beSharedUserId参数有误")
    private long beSharedUserId;//被分享者用户ID

    private double redPacketsMoney;//红包金额，小数点后两位

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//时间

    private String beSharedUserName;//被分享者的用户名称 与数据无关字段

    private String beSharedUserHead;//被分享者的用户头像 与数据无关字段

    private int beSharedProTypeId;     //被分享者 省简称ID  与数据库无关字段

    private long beSharedHouseNumber;//被分享者 门牌号  与数据库无关字段
}
