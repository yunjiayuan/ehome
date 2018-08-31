package com.busi.payment.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;

/** 
 * 支付宝工具类
 *
 * @author SunTianJie 
 *
 * @version create time：2016-11-30 下午3:49:19 
 * 
 */
public class AlipayUtils {

    /***
     * 给支付订单参数加签
     * @param data 订单信息json格式
     * @param notify_url 支付宝平台同步回调地址
     * @return 加签后参数字符串
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    public static String dataSign(String data,String notify_url,long myId) throws AlipayApiException, UnsupportedEncodingException {  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String timestamp = sdf.format(new Date());  
//        notify_url = "http://ephone.lichengwang.com/eps/checkAlipaySign";  
        String content = "app_id=" + AlipayConfig.APP_ID
        			+ "&biz_content=" + data
        			+ "&charset=" + AlipayConfig.ALIPAY_CHARSET
        			+ "&method=" + AlipayConfig.METHOD 
//        			+ "&format=" + AlipayConfig.FORMAT  
        			+ "&notify_url=" + notify_url 
        			+ "&passback_params=" + myId 
        			+ "&sign_type=" + AlipayConfig.SIGN_TYPE 
        			+ "&timestamp=" + timestamp
        			+ "&version=" + AlipayConfig.VERSION;  
        //开始加签
        String sign = AlipaySignature.rsaSign(content, AlipayConfig.APP_PRIVATE_KEY, AlipayConfig.ALIPAY_CHARSET);  
        String result = "app_id=" + encode(AlipayConfig.APP_ID) 
        		+ "&biz_content=" + encode(data) 
        		+ "&charset=" + encode(AlipayConfig.ALIPAY_CHARSET) 
        		+ "&method=" + encode(AlipayConfig.METHOD) 
//        		+ "&format=" + encode(AlipayConfig.FORMAT)  
        		+ "&notify_url=" + encode(notify_url) 
        		+ "&passback_params=" + encode(myId+"") 
        		+ "&sign_type=" + encode(AlipayConfig.SIGN_TYPE) 
                + "&timestamp=" + encode(timestamp) 
                + "&version=" + encode(AlipayConfig.VERSION) 
                + "&sign=" + encode(sign);
  
        return result;  
    }  
    private static String encode(String sign) throws UnsupportedEncodingException {  
        return URLEncoder.encode(sign, "utf-8");
//        		.replace("+", "%2B")
//        		.replace(" ", "%20") 
//        		.replace("/", "%2F")  
//        		.replace("?", "%3F")  
//        		.replace("%", "%25")  
//        		.replace("#", "%23")  
//        		.replace("&", "%26") 
//		        .replace("=", "%3D");  
    } 
    
    public static void main(String[] args) throws AlipayApiException {
    	AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.GATEWAY,AlipayConfig.APP_ID,AlipayConfig.APP_PRIVATE_KEY,"json",AlipayConfig.ALIPAY_CHARSET,AlipayConfig.ALIPAY_PUBLIC_KEY);
    	AlipayTradePayRequest request = new AlipayTradePayRequest();
    	request.setBizContent("{" +
				"\"out_trade_no\":\"20161201180801235846\"," +
				"\"subject\":\"用户10076进行支付宝充值，金额为0.01\"," +
				"\"total_amount\":0.01," +
				"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
				"}");
    	AlipayTradePayResponse response = alipayClient.execute(request);
    	if(response.isSuccess()){
    	System.out.println("调用成功");
    	} else {
    	System.out.println("调用失败");
    	}
	}
}
