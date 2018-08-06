package com.busi.utils;

/** 
 * 本类主要用于提供手机短信平台的相关功能工具
 * 
 * @author SunTianJie 
 *
 * @version create time：2015-7-13 下午1:40:35 
 * 
 */
public class SMSUtil {
	

	/***
	 * 发送短信工具类
	 * @param content 短信内容
	 * @param phones  将要发送的手机号 支持多个手机号发送 格式：15988888888,15977777777
	 */
	public static void sendMessage(String content,String phones) {
		SendPhoneMessage sMessage = new SendPhoneMessage();
		sMessage.setContent(content);
		sMessage.setPhones(phones);
		sMessage.sendPhoneMessage();
	}
}
