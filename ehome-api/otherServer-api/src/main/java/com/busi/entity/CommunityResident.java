package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

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

    private long userId;            //用户ID

    private int identity;            //身份:0普通 1管理员 2创建者

    private int review;            // 0审核中 1已审核

    private int type;            // 加入方式：0正常加入  1被邀请加入

    //与数据库无关字段
    private String name; //用户名

    private String head; //头像

    private int proTypeId;//省简称ID

    private long houseNumber;//门牌号
}
