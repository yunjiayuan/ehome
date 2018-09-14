package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 消息设置
 * @author: ZHaoJiaJie
 * @create: 2018-09-06 17:51
 */
@Setter
@Getter
public class Notice {

    private long id;

    @Min(value = 1, message = "userId参数有误")
    private long userId;//用户

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;//添加时间

    private String exemptingStartTime;//自定义免扰开始时间

    private String exemptingEndTime;//自定义免扰结束时间

    private int shock;		//震动	 0关闭  1开启

    private int voice; //声音 0启用  1关闭

    private int newNotice; // 新消息通知  0启用  1关闭

    private int showContents; // 通知显示消息内容  0显示  1不显示

    private int allDayExempts; // 全天免扰 0关闭  1开启

}
