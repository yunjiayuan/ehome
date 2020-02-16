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

    //与数据库无关字段
    private String userName;//用户名

    private String userHead;//头像

    private int sex;//性别 1男2女

    private int province;//省

    private int city;//城市

    private int district;//地区或县

    private int proTypeId;//省简称ID

    private long houseNumber;// 门牌号
}
