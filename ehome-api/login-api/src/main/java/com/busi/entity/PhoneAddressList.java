package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 手机通讯录实体
 * author：SunTianJie
 * create time：2018/7/18 14:34
 */
@Getter
@Setter
public class PhoneAddressList {

    private long userId;//用户ID

    private String phone; //手机号

    private String name;//用户昵称

    private String head;//用户头像

    private int status;//状态 0不是好友  1是好友  2不是云家园用户

}
