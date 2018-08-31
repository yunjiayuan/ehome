package com.busi.payment.weixin;

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
	
	private String return_msg;
	
	private String appid;
	
	private String mch_id;
	
	private String nonce_str;
	
	private String sign;
	
	private String result_code;
	
	private String prepay_id;
	
	private String trade_type;
	
}
