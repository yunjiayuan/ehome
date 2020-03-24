package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 社区公告
 * author ZJJ
 * Create time 2020-03-23 16:18:09
 */
@Setter
@Getter
public class CommunityNotice {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 居民ID

    private long communityId;    //newsType=0时居委会ID  newsType=1时物业ID

    private int type;         //类别   0居委会  1物业

    private String content;            // 内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;                     //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;                 //刷新时间

}
