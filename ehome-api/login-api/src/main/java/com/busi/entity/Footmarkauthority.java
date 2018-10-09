package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/***
 * 足迹权限实体类
 * author：ZhaoJiaJie
 * create time：2018-9-29 14:26:03
 */
@Setter
@Getter
public class Footmarkauthority {

    private long id;        //主键

    private long userId;    //用户

    private int authority;    // 权限:    0公开  1好友可见  2仅自己可见

}
