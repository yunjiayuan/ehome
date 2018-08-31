package com.busi.payment.alipay;
/** 
 * 支付宝常用配置
 * 
 * @author SunTianJie 
 *
 * @version create time：2016-12-1 下午3:15:30 
 * 
 */
public class AlipayConfig {

	/*支付宝应用ID*/
	public static final String APP_ID = "2016113003623646";  
	/*支付宝应用私钥*/
	public static final String APP_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALklUsTh8UwAVzAnEsU83+v5BJthipdbWX7yF6Xt01wvGGVTW9wh1igGtY1uGJQtrJowRLLkfvU46DLPriEMCr4EWaDM70pR3AbVqrXOuQ3cbn0v9/hbAdL8G56kk6oPxLJS39RtCOnZurIz3ifRqEssMqTyMV02mJZcNVnDyIGLAgMBAAECgYEAif+Xwcfm4o8ebXWyN/E3tdrV5Dq/4jDkdApeNf8eGwe/V0baoSlXRl451EDLcSbaD9MKYYyOVJkl+TlywI8JMXPgxc2vkTr5Lb0D4lDeuDBo6aAqZJo6y5IEMYQpbprbY/iMLOpGR5SBUgpFZ7X522sIse9C7EBmFmQftWyrJIECQQDafXTtiYn6dekGVXrFXJMAtor/wvX3Vgsh12VHYprBbUMJEI/3HnVcLlkunk2L1K22l5EJLT0LoYdd4kxgJwWnAkEA2O5mfkTNnl3v6REldjIWUwi6CzURdV3viAJMRVH/xVvikGmGwPJT0MQ+IIvP5joooMSZsjw/Agh21R8DmPUpfQJBAK405awXw/n9VYUFVtRSEau54G445qEE6+9ZrJkUV8vt8Esj94XTtUOAeP8gnTfmpXM2uh+VAF1rt2D19Gud8XUCQCnvZBbSvCD6Lc+TRfekVRZ2IGjcOGTE5PIY55+a62O+kHy0OZ2A+tznos7t2CG8anAxsTh4VwHQmxA1758Y/Y0CQDHX4BRrvdl0Mp2SPy1ibqvlDCSs4wda4r+6rJRdJT+WN13sAb1cY6uRVA4KVQqyBq4u8AZut+TRPzgfzjR9Py4=";  
	/*支付宝应用公钥*/
	public static final String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";  
	/*支付宝请求使用的编码格式*/
	public static final String ALIPAY_CHARSET = "utf-8";
	/*支付宝接口名称*/
	public static final String METHOD = "alipay.trade.app.pay";  
	/*商户生成签名字符串所使用的签名算法类型*/
	public static final String SIGN_TYPE = "RSA";  
	/*支付宝版本号*/
	public static final String VERSION = "1.0";  
	
	public static final String FORMAT = "json";  
	/*支付宝网关*/
	public static final String GATEWAY = "https://openapi.alipay.com/gateway.do";  
	/*支付宝 充值支付 同步回调服务端接口地址*/
	public static final String RECHARGE_NOTIFY_URL = "http://ephone.lichengwang.com/eps/checkAlipaySign";  
	
}
