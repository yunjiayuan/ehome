package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @program: ehome
 * @description: 生活圈标签
 * @author: ZHaoJiaJie
 * @create: 2018-11-01 16:25
 */
@Setter
@Getter
public class HomeBlogAccess {

    private long id;//主键ID

    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!#￥$%*()\\-]{0,30}", message = "标签格式有误,标签最长为30字，并且不能包含非法字符")
    private String tagName;            //标签名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //创建时间

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;      //创建标签用户

    private String users;    //标签内成员12321,313123,23131

}
