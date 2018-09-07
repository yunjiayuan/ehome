package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @program: 喂鸟互动次数
 * @author: ZHaoJiaJie
 * @create: 2018-09-06 09:38
 */
@Setter
@Getter
public class BirdInteraction {

    private long id;

    @Min(value = 1, message = "myId参数有误")
    private long userId;		//操作者

    @Min(value = 1, message = "userId参数有误")
    private long visitId;	//被喂者

    private long feedBirdTotalCount;	//喂鸟次数

}
