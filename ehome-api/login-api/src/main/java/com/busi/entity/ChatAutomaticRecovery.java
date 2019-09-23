package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 聊天内容自动回复实体
 * author：SunTianJie
 * create time：2019/9/23 15:32
 */
@Getter
@Setter
public class ChatAutomaticRecovery {

    private long userId;//发送者 用户ID

    @Max(value = 2, message = "您设置的消息类型超出可用范围")
    @Min(value= 0 ,message= "您设置的消息类型超出可用范围")
    private  int contentType;//消息类型 0文本 1图片 2音频

    private  String content;//对应的文本内容 或者 图片和音频的地址 绝对地址

}
