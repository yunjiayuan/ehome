package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @program: ehome
 * @description: 意见反馈与投诉
 * @author: ZHaoJiaJie
 * @create: 2018-10-09 16:44
 */
@Setter
@Getter
public class Feedback {

    private long id; // 主键

    @Min(value = 1, message = "userId参数有误")
    private long userId; // 用户ID

    private String content; // 内容

    private String imgUrl;    //图片

    private int sort; // 类别    opinionType=0时为反馈类别：0.聊天相关1.访问串门相关2.房间功能相关3.头像设置及私房照上传4.个人资料编辑5.活动参加及投票6.发布公告相关7.发布家博相关8.求助发布相关
                      // 9.钱包充值相关10.家币家点相关11.红包发送相关12.二手物品相关13.发布视频照片相关14.添加好友及通讯录15.串门送礼物相关16.串门喂鹦鹉砸蛋相关17.每日任务领取家点18.红包分享得红包相关
                      // 19.家门口功能相关 20.设置相关 21.家门口社区相关 22.家门口物业相关 23.其他
    //opinionType=1时为投诉类别：0、聊天互动类1、公共信息类2、家博信息类3、个人资料类4、活动、求助类5、其他类

    private int opinionType;  //意见类型  0：反馈  1：投诉  2：举报

}
