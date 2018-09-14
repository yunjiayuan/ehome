package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description:中奖记录
 * @author: ZHaoJiaJie
 * @create: 2018-09-14 11:06
 */
@Setter
@Getter
public class PrizesLuckyDraw {

    private long id;	//ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//参与用户Id

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;	//参与时间

    private int grade;	//奖品等级：1一等奖  2纪念奖

    private int prize;	//奖品类型：0背包  1便携音箱  2豆浆机  3精美餐具  4动漫模型  5酒红石榴石手链  6女士太阳镜  7剃须刀  8头戴式耳机  9榨汁机

    private int issue;		//期号

    private int winningState;		//中奖状态 0没中 1中奖未领奖  2 已领奖  3已过期

    private String cost;//奖品名称:背包  便携音箱  豆浆机  精美餐具  动漫模型  酒红石榴石手链  女士太阳镜  剃须刀  头戴式耳机  榨汁机

    //与数据库无关字段
    private String name; //用户名	查询后从内存获取最新

    private String head; //头像	查询后从内存获取最新

    private int proTypeId;	 //	省简称ID

    private long houseNumber;// 门牌号
}
