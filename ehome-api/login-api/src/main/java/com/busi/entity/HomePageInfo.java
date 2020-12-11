package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 家主页实体类
 * author：SunTianJie
 * create time：2018/7/16 14:40
 */
@Getter
@Setter
public class HomePageInfo {

    private long userId;//被访者的用户ID

    private int membershipLevel;//用户当前会员状态  1：普通会员  	2：vip高级会员  3：元老级会员  4：创始元老级会员

    private int memberLevel;//会员等级  1：一级  2：二级 3...

    private String head;//头像

    private int graffitiStatus;//涂鸦状态 0当前用户未被涂鸦过 1当前用户已被涂鸦

    private int proType;//省简称ID

    private long houseNumber;//门牌号

    private String name;//昵称

    private int sex;//性别

    private int isNewUser;//是否为新用户  0新用户 1已领新人红包(老用户)

    private int welcomeInfoStatus;//系统欢迎消息状态 0表示未发送  1表示已发送

    private int isFriend;//0不是好友  1是好友

    private int flag;//临时参数 1禁止查看苹果部分功能 方便IOS平台审核

    private int flagByAndroid;//临时参数 1禁止查看安卓部分功能 方便安卓平台审核

    private int videoshootType;//临时参数 “生活圈拍摄视频时的拍摄类型” 0默认使用七牛拍摄 1使用APP自研拍摄 2使用其他平台拍摄

    private long garden;//花园锁状态 1未上锁 2已上锁

    private long livingRoom;//客厅锁状态 1未上锁 2已上锁

    private long homeStore;//家店锁状态 1未上锁 2已上锁

    private long storageRoom;//存储室锁状态 1未上锁 2已上锁

    private int accessRights;//访问权限 0允许任何人  1禁止任何人  2 已是好友可以访问   3不是好友禁止访问

    private int switchLamp;//开关灯状态值 0 默认开灯  1关灯

    private int isFollow;//0未关注  1已关注

    private long followCounts;//粉丝数

    private int isGoodNumber;//是否为靓号（0：普通号，1：靓号）

    private int user_ce;//用户认证 ，0表示未经过认证，1表示通过网络大V认证

    private long totalVisitCount;//总访问量  与数据库无关字段

    private long todayVisitCount;//今天访问量 与数据库无关字段

    private int purseCashOutStatus;//临时参数 与数据库无关字段 0开启钱包提现功能 1禁用钱包提现功能

    private int talkToSomeoneStatus;//倾诉状态 0表示不接受倾诉  1表示接受倾诉

    private int chatnteractionStatus;//聊天互动功能的状态 0表示不接受别人找你互动  1表示接受别人找你互动
}
