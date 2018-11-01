package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 生活圈 点赞 实体类
 * author：SunTianJie
 * create time：2018/11/1 18:46
 */
@Setter
@Getter
public class HomeBlogLike {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;//点赞用户ID

    @Min(value = 1, message = "blogId参数有误，超出指定范围")
    private long blogId;//生活圈ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//发布时间

    private String userName; //点赞用户名	 与数据库无关字段
    private String userHead; //点赞用户头像	 与数据库无关字段
}
