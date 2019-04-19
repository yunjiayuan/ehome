package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 家族问候
 * @author: ZHaoJiaJie
 * @create: 2019-04-18 11:17
 */
@Setter
@Getter
public class FamilyGreeting {

    private long id;			//主键

    private long userId;		//当前用户ID

    private long visitUserId;		//来访用户ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;				//问候的时间

    //与数据库无关字段
    private String name; 			    //用户名

    private String head; 			   		//头像
}
