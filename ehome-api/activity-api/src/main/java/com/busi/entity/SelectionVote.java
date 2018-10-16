package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 活动投票
 * @author: ZHaoJiaJie
 * @create: 2018-10-10 12:01:02
 */
@Setter
@Getter
public class SelectionVote {

    private long id;

    private long myId;

    private long userId;

    private int selectionType;//活动类型  1城市小姐  2城市之星 3青年创业

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    private String userName;//用户名	查询后从内存获取最新  与数据库无关字段

    private String userHead;//头像	查询后从内存获取最新  与数据库无关字段

    private int sex;//性别 1男2女

    private int province;//省

    private int city;//城市

    private int district;//地区或县

    private int proTypeId;//省简称ID  与数据库无关字段

    private long houseNumber;// 门牌号  与数据库无关字段

}
