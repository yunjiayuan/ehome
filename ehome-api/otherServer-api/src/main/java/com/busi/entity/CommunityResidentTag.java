package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 居民身份标签实体
 * author ZJJ
 * Create time 2020-03-30 11:52:47
 */
@Setter
@Getter
public class CommunityResidentTag {

    private long id;                //主建ID

    private long communityId;    //居委会ID

    @Length(max = 10, message = "tagName标签名字不能超过10个字")
    @Length(min = 1, message = "tagName标签名字不能少于1个字")
    private String tagName;//标签名称

}
