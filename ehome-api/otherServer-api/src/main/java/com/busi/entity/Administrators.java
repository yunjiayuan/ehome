package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 管理员实体
 * @author: ZhaoJiaJie
 * @create: 2020-09-27 13:59:16
 */
@Setter
@Getter
public class Administrators {

    private long id;                    // 主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 用户ID

    private int levels;                //级别：0普通管理员 1高级管理员 2最高管理员

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;	//设置时间

    //与数据库无关字段
    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号
}
