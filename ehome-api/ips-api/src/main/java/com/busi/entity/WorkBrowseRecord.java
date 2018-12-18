package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 简历浏览记录实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkBrowseRecord {

    private long id;        //主键

    private long resumeId;        //被浏览简历ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //浏览者ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //浏览时间

    //与数据库无关字段
    private String head;        //头像

    private int age;        //年龄

    private int sex;        //性别

    private long houseNumber;        //门牌号

    private int proTypeId;                //	省简称ID

}
