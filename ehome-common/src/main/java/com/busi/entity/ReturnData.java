package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 返回数据实体类
 * author：SunTianJie
 * create time：2018/5/29 15:46
 */
@Setter
@Getter
public class ReturnData {

    private Integer statusCode;//状态码

    private String statusMsg;//状态信息

    private Object data;//返回数据对象

}
