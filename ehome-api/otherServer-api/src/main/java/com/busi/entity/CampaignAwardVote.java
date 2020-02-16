package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 战役评奖活动投票
 * @author: ZHaoJiaJie
 * @create: 2020-02-16 13:12:33
 */
@Setter
@Getter
public class CampaignAwardVote {

    private long id;

    private long myId;    //投票者

    private long userId;   //被投票者

    private long campaignAwardId;   //活动ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

}
