package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 脚印实体类
 * author：SunTianJie
 * create time：2018/9/12 17:18
 */
@Getter
@Setter
public class Footprint {

    private long id;		//主见

    private long myId;		//当前登录用户ID（主动串门拜访的用户）

    private long userId;	//被拜访用户

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;		//拜访时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date awayTime;	//离去时间

    private String userName;	//来访用户名称 -与数据库无关

    private String userHead;	//来访用户头像 -与数据库无关

    private int sex;// 性别 :1男 2女 -与数据库无关

    private int age;//年龄  -与数据库无关

}
