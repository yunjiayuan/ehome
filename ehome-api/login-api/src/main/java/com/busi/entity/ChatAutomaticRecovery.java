package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * 聊天内容自动回复实体
 * author：SunTianJie
 * create time：2019/9/23 15:32
 */
@Getter
@Setter
public class ChatAutomaticRecovery {

    private long userId;//发送者 用户ID

    @Max(value = 53870, message = "接收消息的用户ID不在自动回复范围之内")
    @Min(value= 13870 ,message= "接收消息的用户ID不在自动回复范围之内")
    private long recoveryUserId;//接收者 用户ID

    @Max(value = 2, message = "您设置的消息类型超出可用范围")
    @Min(value= 0 ,message= "您设置的消息类型超出可用范围")
    private  int contentType;//消息类型 0文本 1图片 2音频

    @NotEmpty(message ="聊天内容不能为空")
    private  String content;//对应的文本内容 或者 图片和音频的地址 绝对地址

    private  String session_id;//服务端返回的标识符 需要客户端下次请求携带

}
