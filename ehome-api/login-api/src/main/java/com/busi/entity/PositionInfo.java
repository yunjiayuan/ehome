package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 用户坐标位置信息实体
 * author：SunTianJie
 * create time：2018/7/12 16:28
 */
@Setter
@Getter
public class PositionInfo {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//用户ID

    @Digits(integer=3, fraction=6,message = "经度格式不正确")
    private double lon;//经度 小数点后6位

    @Digits(integer=3, fraction=6,message = "纬度格式不正确")
    private double lat;//纬度 小数点后6位

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//更新时间

}
