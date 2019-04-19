package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 家族评论
 * @author: ZHaoJiaJie
 * @create: 2019-04-18 11:17
 */
@Setter
@Getter
public class FamilyComments {

    private long id;			//主键

    private long myId;		//登入者ID

    private long userId;		//评论用户ID

    private String content;			//评论的内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;				//评论的时间

    private int state;				//状态 0正常 1删除

    //与数据库无关字段
    private String name; 			    //用户名

    private String head; 			   		//头像
}
