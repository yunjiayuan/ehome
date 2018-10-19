package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: 存储室相册密码
 * @author: ZHaoJiaJie
 * @create: 2018-10-18 10:58:47
 */
@Setter
@Getter
public class Homealbumpwd {

    private long id;

    private String password;//用户设置的一级密码

    private String status;//随机生成的状态码

}
