package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @program: ehome
 * @description: 生活圈兴趣标签
 * @author: suntj
 * @create: 2018-11-02 16:58
 */
@Setter
@Getter
public class HomeBlogTag {

    private long id;//主建ID

    @Min(value = 0, message = "tagValue参数有误，超出最小取值范围")
    @Max(value = 48, message = "tagValue参数有误，超出最大取值范围")
    private int tagValue;//标签值

    @Length(max = 10, message = "tagName标签名字不能超过10个字")
    @Length(min = 1, message = "tagName标签名字不能少于1个字")
    private String tagName;//标签名称

    @Min(value = 0, message = "status超出最小取值范围")
    @Max(value = 1, message = "status超出最大取值范围")
    private int status;//显示状态 0正常显示 1隐藏

    @Min(value = 0, message = "orderType超出最小取值范围")
    @Max(value = 1, message = "orderType超出最大取值范围")
    private int orderType;//显示顺序 数值越小显示级别越高
}
