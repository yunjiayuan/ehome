package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 档期时长记录
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 16:58
 */
@Setter
@Getter
public class SelfChannelDuration {

    private long id;

    private int timeStamp;   //时间戳  （20190325）

    private int surplusTime;  //剩余时长(初始一天秒数86400)

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date nextTime;  //下一个排挡时间 （播放时间20190325121212）
}
