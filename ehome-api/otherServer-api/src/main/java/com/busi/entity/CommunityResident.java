package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 居民
 * author ZJJ
 * Create time 2020-03-16 17:16:58
 */
@Setter
@Getter
public class CommunityResident {

    private long id;                //主键ID

    private long communityId;    //居委会ID

    private long masterId;    //邀请者ID

    private long userId;            //居民ID

    @Min(value = 0, message = "identity参数有误，数值超出指定范围")
    @Max(value = 2, message = "identity参数有误，数值超出指定范围")
    private int identity;            //身份:0普通 1管理员 2创建者

    private int review;            // 0审核中 1已审核

    private int type;            // 加入方式：0主动加入  1被邀请加入

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            // 加入时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;                 //刷新时间

    //与数据库无关字段
    private String userIds;     //被邀请加入时多个居民逗号分隔

    private String name; //用户名

    private String head; //头像

    private int proTypeId;//省简称ID

    private long houseNumber;//门牌号
}
