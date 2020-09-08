package com.busi.payment.weixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.busi.entity.WeixinSignBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.busi.utils.CommonUtils;

/**
 * 微信支付相关工具类
 * 
 * @author SunTianJie
 * 
 * @version create time：2016-12-12 下午1:59:05
 * 
 */
public class WeixinUtils {

    
    /***
     * 给指定数据加签，微信根据参数字段的ASCII码值进行排序 加密签名,故使用SortMap进行参数排序
//     * @param characterEncoding
     * @param parameters
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static String dataSign(SortedMap<String,String> parameters){
    	//加签主逻辑
        StringBuffer sb = new StringBuffer();
        String sign = "";
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WeixinConfig.PARTNER_KEY);//最后加密时添加商户密钥，由于key值放在最后，所以不用添加到SortMap里面去，单独处理，编码方式采用UTF-8
        sign = CommonUtils.strToMD5(sb.toString(),32).toUpperCase();
        return sign;
    }
    /***
     * 加签完毕，开始组合数据请求微信
     * @return
     */
    public static WeixinSignBean getWeixinSignBean(String sign, SortedMap<String,String> parameters){
        parameters.put("sign", sign);
		String requestXml = WeixinUtils.getRequestXml(parameters);//生成Xml格式的字符串
		if(requestXml==null){
			return null;
		}
		String result = WeixinUtils.httpsRequest(WeixinConfig.WEIXIN_URL, "POST", requestXml);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+result);
		WeixinSignBean wsb = null;
		if(!CommonUtils.checkFull(result)){			
			wsb = parse(result);			
		}
		return wsb;
    }
    
    /***
	 * 将封装好的参数转换成Xml格式类型的字符串
	 * @param parameters
	 * @return
	 */
    @SuppressWarnings("rawtypes")
	public static String getRequestXml(SortedMap<String,String> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if("sign".equalsIgnoreCase(k)){

            }
            else if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)) {
                sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
            }
            else {
                sb.append("<"+k+">"+v+"</"+k+">");
            }
        }
        sb.append("<"+"sign"+">"+"<![CDATA["+parameters.get("sign")+"]]></"+"sign"+">");
        sb.append("</xml>");
        String result = "";
        try {
			result = new String(sb.toString().getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return result;
    }

    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
    	OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes(WeixinConfig.WEIXIN_CHARSET));
                outputStream.close();
            }
            // 从输入流读取返回内容
            inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, WeixinConfig.WEIXIN_CHARSET);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException e) {
        	e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }finally{
        	if(outputStream!=null){
        		try {
					outputStream.close();
					outputStream =null;
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(inputStream!=null){
        		try {
        			inputStream.close();
        			inputStream =null;
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        }
        return null;
    }

    /**
     * 解析微信返回的数据
     * @param protocolXML
     * @return
     */
	public static WeixinSignBean parse(String protocolXML) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(protocolXML)));
			Element root = doc.getDocumentElement();
			NodeList books = root.getChildNodes();
			WeixinSignBean wsb = new WeixinSignBean();
			if (books != null) {
				for (int i = 0; i < books.getLength(); i++) {
					Node book = books.item(i);
					if("return_code".equals(book.getNodeName())){
						wsb.setReturn_code(book.getFirstChild().getNodeValue());
					}else if("return_msg".equals(book.getNodeName())){
						wsb.setReturn_msg(book.getFirstChild().getNodeValue());
					}else if("appid".equals(book.getNodeName())){
						wsb.setAppid(book.getFirstChild().getNodeValue());
					}else if("mch_id".equals(book.getNodeName())){
						wsb.setMch_id(book.getFirstChild().getNodeValue());
					}else if("nonce_str".equals(book.getNodeName())){
						wsb.setNonce_str(book.getFirstChild().getNodeValue());
					}else if("sign".equals(book.getNodeName())){
						wsb.setSign(book.getFirstChild().getNodeValue());
					}else if("result_code".equals(book.getNodeName())){
						wsb.setResult_code(book.getFirstChild().getNodeValue());
					}else if("prepay_id".equals(book.getNodeName())){
						wsb.setPrepay_id(book.getFirstChild().getNodeValue());
					}else if("trade_type".equals(book.getNodeName())){
						wsb.setTrade_type(book.getFirstChild().getNodeValue());
					}else{
						
					}
				}
			}
			return wsb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}  

//    public static void main(String[] args) {
////		 SortedMap<String, String> signParams = new TreeMap<String, String>();
////		 signParams.put("appid", WeixinConfig.APP_ID);//app_id
////		 signParams.put("body","中国");//商品参数信息
////		 signParams.put("mch_id", WeixinConfig.MCH_ID);//微信商户账号
////		 signParams.put("nonce_str", CommonUtils.strToMD5(CommonUtils.getRandom(16), 32));//32位不重复的编号 随机字符串
////		 signParams.put("notify_url", WeixinConfig.RECHARGE_NOTIFY_URL);//回调页面
////		 signParams.put("out_trade_no", "20161212190933548693033");//商户订单号
////		 signParams.put("spbill_create_ip","192.168.1.126");//用户端实际ip
////		 signParams.put("total_fee","1");//支付金额 单位为分
////		 signParams.put("trade_type", "APP");//付款类型为APP
////		 signParams.put("attach", "ID");//附加数据 用户参数传递
////		 String sign = WeixinUtils.dataSign(WeixinConfig.WEIXIN_CHARSET, signParams);//生成签名
////    	SortedMap<String, String> signParams = new TreeMap<String, String>();
////		signParams.put("appid", WeixinConfig.APP_ID);//app_id
////		signParams.put("body","用户[10076进行充值业务,金额为:0.01");//商品参数信息
////		signParams.put("mch_id", WeixinConfig.MCH_ID);//微信商户账号
////		signParams.put("nonce_str", CommonUtils.strToMD5(CommonUtils.getRandom(16), 32));//32位不重复的编号 随机字符串
////		signParams.put("notify_url", WeixinConfig.RECHARGE_NOTIFY_URL);//回调页面
////		signParams.put("out_trade_no", "20161212190933548693033301");//商户订单号
////		signParams.put("spbill_create_ip","");//用户端实际ip
////		signParams.put("total_fee","1");//支付金额 单位为分
////		signParams.put("trade_type", "APP");//付款类型为APP
////		signParams.put("attach", "10076");//附加数据 用户参数传递
////		String sign = WeixinUtils.dataSign( signParams);//生成签名33572FB97CAC7212EFC15FD597AED281
////		WeixinSignBean wsb = WeixinUtils.getWeixinSignBean(sign, signParams);
////		long time = System.currentTimeMillis()/1000;
////		SortedMap<String, String> signParams2 = new TreeMap<String, String>();
////		System.out.println(wsb.getSign());
////		
//////		signParams2.put("appid", WeixinConfig.APP_ID);//app_id
//////		signParams2.put("mch_id", WeixinConfig.MCH_ID);//微信商户账号
//////		signParams2.put("prepay_id", wsb.getPrepay_id());//微信商户账号
//////		signParams2.put("nonce_str", wsb.getNonce_str());//回调页面
//////		signParams2.put("timestamp", time+"");//商户订单号
////		signParams2.put("appid", WeixinConfig.APP_ID);//app_id
////		signParams2.put("partnerid", WeixinConfig.MCH_ID);//微信商户账号
////		signParams2.put("prepayid", wsb.getPrepay_id());//微信商户账号
////		signParams2.put("noncestr", wsb.getNonce_str());//回调页面
////		signParams2.put("timestamp", time+"");//商户订单号
////		signParams2.put("package", "Sign=WXPay");//商户订单号
////		System.out.println(WeixinUtils.dataSign( signParams2));
//    	
//    	
//    	
//    	String ssString = "<xml><appid><![CDATA[wx4cf8d1cd16fee6d4]]></appid><attach><![CDATA[10076]]></attach><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1400030002]]></mch_id><nonce_str><![CDATA[aea9049f47f4bb4b03bb831f2d6d4484]]></nonce_str><openid><![CDATA[o6NMRv0tWDHHPhN_nhkICAgD9Xb0]]></openid><out_trade_no><![CDATA[1481694553444266843]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[EE8C88A06737071DD89D0F9C84B1C34A]]></sign><time_end><![CDATA[20161214134920]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4003812001201612142761226261]]></transaction_id></xml>";
//    	Document doc = null;
//    	Element root = null;
//		try {
//			doc = DocumentHelper.parseText(ssString);
//			root = doc.getRootElement();// 指向根节点    
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        //开始对微信数据和订单数据进行重新加签 并比对签名
//        SortedMap<String, String> signParams = new TreeMap<String, String>();
//    	signParams.put("appid", root.element("appid").getText().trim());//app_id
//    	signParams.put("attach", root.element("attach").getText().trim());//app_id
//    	signParams.put("bank_type", root.element("bank_type").getText().trim());//app_id
//    	signParams.put("cash_fee", root.element("cash_fee").getText().trim());//app_id
//    	signParams.put("fee_type", root.element("fee_type").getText().trim());//app_id
//    	signParams.put("is_subscribe", root.element("is_subscribe").getText().trim());//app_id
//    	signParams.put("mch_id", root.element("mch_id").getText().trim());//app_id
//    	signParams.put("nonce_str", root.element("nonce_str").getText().trim());//app_id
//    	signParams.put("openid", root.element("openid").getText().trim());//app_id
//    	signParams.put("out_trade_no", root.element("out_trade_no").getText().trim());//app_id
//    	signParams.put("result_code", root.element("result_code").getText().trim());//app_id
//    	signParams.put("return_code", root.element("return_code").getText().trim());//app_id
////    	signParams.put("sign", root.element("sign").getText());//app_id
//    	signParams.put("time_end", root.element("time_end").getText().trim());//app_id
//    	signParams.put("total_fee", root.element("total_fee").getText().trim()+"");//app_id
//    	signParams.put("trade_type", root.element("trade_type").getText().trim()+"");//app_id
//    	signParams.put("transaction_id", root.element("transaction_id").getText().trim());//app_id
//    	//重新加签
//		String newSign = WeixinUtils.dataSign(signParams);//生成签名
//		System.out.println("一次重新加签："+newSign);
//	}
}
