package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 喂鸟详细记录
 * @author: ZHaoJiaJie
 * @create: 2018-09-04 13:52
 */
@Setter
@Getter
public class BirdFeedingData {

    private long id;

    @Min(value = 1, message = "userId参数有误")
    private long userId;                //玩家id

    private long feedBirdTotalCount;    //累计玩家喂鸟总次数

    private long beenFeedBirdTotalCount;//累计玩家被喂鸟总次数

    private long lastFeedBirdDate;        //记录玩家最后喂鸟日(判断是否同一天)

    private long curFeedBirdTimes;        //记录玩家今日喂鸟已用次数

    private String feedBirdIds;        //记录玩家今日喂过的鸟

    private long birdBeFeedTotalCount;    //记录玩家今日自家鸟被喂次数(判断是否喂饱)

    private long beenLastFeedBirdDate;    //记录玩家最后被喂鸟日(判断是否同一天)

    private long layingTotalCount;        //记录玩家鹦鹉总产蛋数

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startLayingTime;        //鹦鹉喂饱时间(产蛋时间)

    private int eggState;                //蛋状态  0 没蛋  1产蛋中 2已产

}
