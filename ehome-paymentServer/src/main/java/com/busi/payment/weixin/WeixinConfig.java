package com.busi.payment.weixin;
/** 
 *
 * @author SunTianJie 
 *
 * @version create time：2016-12-12 下午1:31:52 
 * 
 */
public class WeixinConfig {

	/*微信应用ID*/
	public static final String APP_ID = "wx4cf8d1cd16fee6d4";
	/*微信商户号*/
	public static final String MCH_ID = "1400030002";  
	/*微信请求使用的编码格式*/
	public static final String WEIXIN_CHARSET = "UTF-8";
	/*微信App支付统一下单接口地址*/
	public static final String WEIXIN_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";  
	/*商户生成签名字符串所使用的签名算法类型*/
	public static final String SIGN_TYPE = "MD5";  
	/*微信私钥*/
	public static final String PARTNER_KEY = "ad0a7f04730737233175a98958d1db4e";  
	/*微信 充值支付 同步回调服务端接口地址*/
	public static final String RECHARGE_NOTIFY_URL = "http://ephone.lichengwang.com/eps/checkWeixinSign";  
}
