package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 自频道时长记录
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 16:58
 */
@Setter
@Getter
public class SelfChannelDuration {

    private long id;

    private int timeStamp;   //时间戳

    private long surplusTime;  //剩余时长
}
