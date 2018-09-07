package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 聊天系统内发送红包功能 实体类（点对点发送）
 * 本类充当红包订单实体
 * author：SunTianJie
 * create time：2018/9/6 19:22
 */
@Getter
@Setter
public class RedPacketsInfo {

    private long id;//主键ID

    @Min(value= 1 ,message= "sendUserId参数有误")
    private long sendUserId;//发红包者ID

    @Min(value= 1 ,message= "receiveUserId参数有误")
    private long receiveUserId;//接收红包者ID

    @DecimalMax(value = "200",message = "红包最大金额为200元")
    @DecimalMin(value = "0.01",message = "红包最大金额为0.01元")
    private double redPacketsMoney;//红包金额，最大200 小数点后两位

    @Length(max = 25, message = "sendMessage参数有误，留言字数太多了")
    private String sendMessage;//发送红包留言

    @Length(max = 25, message = "receiveMessage参数有误，留言字数太多了")
    private String receiveMessage;//接收红包留言

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date sendTime;//发送红包时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date receiveTime;//接收红包时间

    @Max(value = 3, message = "delStatus参数有误，超出指定范围")
    @Min(value= 0 ,message= "delStatus参数有误，超出指定范围")
    private int delStatus;//删除状态 0正常 1表示发布红包者已删除 2表示接受者已删除  3表示双方均已删除该记录

    @Max(value = 1, message = "payStatus参数有误，超出指定范围")
    @Min(value= 0 ,message= "payStatus参数有误，超出指定范围")
    private int payStatus;//发送红包者的支付状态  0未支付 1已支付

    @Max(value = 2, message = "redPacketsStatus参数有误，超出指定范围")
    @Min(value= 0 ,message= "redPacketsStatus参数有误，超出指定范围")
    private int redPacketsStatus;//红包状态 0正常（已发送，未拆收） 1过期 2已拆（已接收）

    private String sendUserName;//发送者的用户名称 与数据无关字段

    private String sendUserHead;//发送者的用户头像 与数据无关字段

    private String receiveUserName;//接收者的用户名称 与数据无关字段

    private String receiveUserHead;//接收者的用户头像 与数据无关字段

}
