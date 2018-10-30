package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 关注信息实体
 * author：SunTianJie
 * create time：2018/10/30 13:59
 */
@Setter
@Getter
public class FollowInfo {

    private long id;//主键ID

    @Min(value= 1 ,message= "userId参数有误，超出指定范围")
    private long userId;//主动关注者ID

    @Min(value= 1 ,message= "userId参数有误，超出指定范围")
    private long followUserId;//被关注者ID

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//发布时间

    private String name;//用户名 有数据库无关字段

    private String head;//头像 有数据库无关字段

}
