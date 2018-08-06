package com.busi.adapter;


import com.alibaba.fastjson.JSONObject;

/**
 * 全平台统一发送消息接口
 * 消息类型：邮件、短信、注册转发、同步信息等业务功能
 * author：SunTianJie
 * create time：2018/5/29 14:24
 */
public interface MessageAdapter {
	void sendMsg(JSONObject body);
}