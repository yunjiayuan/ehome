package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: 儿童/青少年模式密码
 * @author: ZHaoJiaJie
 * @create: 2020-06-16 16:28:44
 */
@Setter
@Getter
public class ChildModelPwd {

    private long id;

    private long userId;//用户Id

    private String password;//密码（MD5）

    private String status;//随机生成的状态码

//    private int state;//状态：0未开启  1已开启

    //与数据库无关
    private String oldPassword;        //原密码
}
