package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

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
}
