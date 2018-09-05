package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 本类用于接收微信加签后返回的数据对象
 * 
 * @author SunTianJie 
 *
 * @version create time：2016-12-12 下午3:56:39 
 * 
 */
@Getter
@Setter
public class WeixinSignBean {

	private String return_code;

	private String appid;

	private String mch_id;

	private String nonce_str;

	private String sign;

	private String result_code;

	private String attach;//此为用户自定义参数 目前只传 用户ＩＤ

	private String trade_type;

	private String return_msg;

	private String prepay_id;

	private String total_fee;

	private String out_trade_no;

	private String bank_type;

	private String cash_fee;

	private String fee_type;

	private String is_subscribe;

	private String openid;

	private String time_end;

	private String transaction_id;

	
}
