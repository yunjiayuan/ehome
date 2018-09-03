package com.busi.payment.unionpay;
/** 
 * 银联支付常用配置
 *
 * @author SunTianJie 
 *
 * @version create time：2016-12-19 下午4:38:10 
 * 
 */
public class UnionPayConfig {
	
	/*商户号 全渠道固定值*/
	public static final String MERID = "802110048160911";
	/*版本号 全渠道固定值*/
	public static final String VERSION = "5.0.0";
	/*字符集编码 可以使用UTF-8,GBK两种方式 */
	public static final String ENCODING_UTF8 = "UTF-8";  
	/*签名方法 目前只支持01：RSA方式证书加密*/
	public static final String SIGNMETHOD = "01";  
	/*交易类型 01:消费*/
	public static final String TXNTYPE = "01";  
	/*交易子类 01：消费*/
	public static final String TXNSUBTYPE = "01";  
	/*填写000201*/
	public static final String BIZTYPE = "000201";  
	/*渠道类型 08手机*/
	public static final String CHANNELTYPE = "08";  
	/*服务端的回调地址*/
	public static final String BACK_URL = "http://ehome.lichengwang.com:8760/paymentServer-api/checkUnionPaySign";//受理方和发卡方自选填写的域[O]--后台通知地址

}
