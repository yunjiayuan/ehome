package com.busi.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.busi.entity.RealNameInfo;
import com.busi.entity.UserBankCardInfo;
import lombok.extern.slf4j.Slf4j;

/** 
 * 用户实名工具类
 *
 * @author SunTianJie 
 *
 * @version create time：2017-6-5 下午1:37:14 
 * 
 */
@Slf4j
public class RealNameUtils {

	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

	//配置您申请的KEY
	public static final String APPKEY ="";
	/****
	 * 实名认证主逻辑
	 * @param userId
	 * @param realName
	 * @param cardNo
	 * @return
	 */
	public static RealNameInfo checkRealName(long userId, String realName, String cardNo){
		String strUrl = Constants.CHECK_REALNAME_URL;
		Map map = new HashMap();
		map.put("key",Constants.REALNAME_KEY);
		map.put("idcard",cardNo);
		map.put("realname",realName);
		RealNameInfo realNameInfo = null;
		try {
			String resString = net(strUrl,map,"GET");
			if(CommonUtils.checkFull(resString)){
				return null;
			}
			JSONObject jsonObj = JSONObject.parseObject(resString);
			if(jsonObj!=null){
				int error_code = -1;
				String reason = "";
				error_code = jsonObj.getInteger("error_code");//状态码
				reason = jsonObj.getString("reason");//状态码
				if(error_code==0){//接口访问成功
					JSONObject resultJsonObj = jsonObj.getJSONObject("result");
					if(resultJsonObj!=null){
						int res = resultJsonObj.getInteger("res");
						if(res==1){//验证成功
							realNameInfo = new RealNameInfo();
							realNameInfo.setUserId(userId);
							realNameInfo.setRealName(realName);
							realNameInfo.setCardNo(cardNo);
//							if(detailsJsonObj.getInteger("sex")==0){//女
//								realNameInfo.setSex(2);
//							}else{//男
//								realNameInfo.setSex(1);
//							}
//							realNameInfo.setBirth(detailsJsonObj.getString("birth"));
//							realNameInfo.setAddrCode(detailsJsonObj.getString("addrCode"));
//							realNameInfo.setCheckBit(detailsJsonObj.getString("checkBit"));
//							realNameInfo.setLength(detailsJsonObj.getInteger("length"));
//							realNameInfo.setAddr(detailsJsonObj.getString("addr"));
//							realNameInfo.setProvince(detailsJsonObj.getString("province"));
//							realNameInfo.setCity(detailsJsonObj.getString("city"));
//							realNameInfo.setArea(detailsJsonObj.getString("area"));
							realNameInfo.setTime(new Date());
						}else{
							log.info("服务端访问“聚合数据实名认证机构”成功，实名认证信息不匹配！");
							return null;
						}
					}else{
						log.info("服务端访问“聚合数据实名认证机构”失败，认证机构响应数据机构解析异常！");
						return null;
					}
				}else{
					log.info("服务端访问“聚合数据实名认证机构”失败，状态码："+error_code);
					return null;
				}
			}else{
				log.info("服务端访问“聚合数据实名认证机构”失败，认证机构响应数据异常！");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("服务端访问“聚合数据实名认证机构”失败，认证机构响应数据异常！！！");
		}
		return realNameInfo;
	}

	/**
	 * 银行卡四元素认证主逻辑
	 * @param userId   当前用户ID
	 * @param bankCard 银行卡号
	 * @param realName 银行卡对应真实姓名
	 * @param cardNo   银行卡对应身份证号
	 * @param phone    银行卡对应手机号
	 * @return
	 */
	public static UserBankCardInfo checkBankCard(long userId, String bankCard, String realName, String cardNo, String phone) {
		UserBankCardInfo userBankCardInfo = null;
		String strUrl = Constants.CHECK_BANKCARD_URL;
		Map map = new HashMap();
		map.put("key",Constants.BANKCARD_KEY);
		map.put("bankcard",bankCard);
		map.put("realname",realName);
		map.put("idcard",cardNo);
		map.put("mobile",phone);
		try {
			String resString = net(strUrl,map,"GET");
			if(CommonUtils.checkFull(resString)){
				return null;
			}
			JSONObject jsonObj = JSONObject.parseObject(resString);
			if(jsonObj!=null){
				int error_code = -1;
				String reason = "";
				error_code = jsonObj.getInteger("error_code");//状态码
				reason = jsonObj.getString("reason");//状态码
				if(error_code==0){//接口访问成功
					JSONObject resultJsonObj = jsonObj.getJSONObject("result");
					if(resultJsonObj!=null){
						int res = resultJsonObj.getInteger("res");
						if(res==1){//验证成功
							userBankCardInfo = new UserBankCardInfo();
							userBankCardInfo.setUserId(userId);
							userBankCardInfo.setBankCard(bankCard);
							userBankCardInfo.setBankCardNo(cardNo);
							userBankCardInfo.setBankName(realName);
							userBankCardInfo.setBankPhone(phone);
							userBankCardInfo.setTime(new Date());
						}else{
							log.info("服务端访问“聚合数据银行卡认证机构”成功，实名认证信息不匹配！");
							return null;
						}
					}else{
						log.info("服务端访问“聚合数据银行卡认证机构”失败，认证机构响应数据机构解析异常！");
						return null;
					}
				}else{
					log.info("服务端访问“聚合数据银行卡认证机构”失败，状态码："+error_code);
					return null;
				}
			}else{
				log.info("服务端访问“聚合数据银行卡认证机构”失败，认证机构响应数据异常！");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("服务端访问“聚合数据银行卡认证机构”失败，认证机构响应数据异常！！！");
		}
		return userBankCardInfo;
	}

	/**
	 * 对接第三方平台
	 * @param strUrl 请求地址
	 * @param params 请求参数
	 * @param method 请求方法
	 * @return  网络请求字符串
	 * @throws Exception
	 */
	public static String net(String strUrl, Map params, String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if(method==null || method.equals("GET")){
				strUrl = strUrl+"?"+urlencode(params);
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if(method==null || method.equals("GET")){
				conn.setRequestMethod("GET");
			}else{
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params!= null && method.equals("POST")) {
				try {
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(urlencode(params));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}
	//将map型转为请求参数型
	public static String urlencode(Map<String,Object>data) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
//	public static RealNameInfo checkRealName(long userId, String realName, String cardNo){
//		RealNameInfo realNameInfo = null;
//		String param="key="+Constants.REALNAME_KEY+"&cardNo="+cardNo+"&realName="+realName+"&information=1";
//		String returnStr = null; // 返回结果定义
//		URL url = null;
//		HttpURLConnection httpURLConnection = null;
//
//		try {
//			url = new URL(Constants.CHECK_REALNAME_URL);
//			httpURLConnection = (HttpURLConnection) url.openConnection();
//			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
//			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			httpURLConnection.setDoOutput(true);
//			httpURLConnection.setDoInput(true);
//			httpURLConnection.setRequestMethod("POST"); // post方式
//			httpURLConnection.connect();
//			//POST方法时使用
//			byte[] byteParam = param.getBytes("UTF-8");
//			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
//			out.write(byteParam);
//			out.flush();
//			out.close();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
//			StringBuffer buffer = new StringBuffer();
//			String line = "";
//			while ((line = reader.readLine()) != null) {
//				buffer.append(line);
//			}
//			reader.close();
//			returnStr = buffer.toString();
//			//解析json
//			JSONObject jsonObj = JSONObject.parseObject(returnStr);
//			if(jsonObj!=null){
//				int error_code = -1;
//				String reason = "";
//				error_code = jsonObj.getInteger("error_code");//状态码
//				reason = jsonObj.getString("reason");//状态码
//				if(error_code==0&&"认证通过".equals(reason)){//认证通过
//					JSONObject resultJsonObj = jsonObj.getJSONObject("result");
//					if(resultJsonObj!=null){
//						JSONObject detailsJsonObj = resultJsonObj.getJSONObject("details");
//						if(detailsJsonObj!=null){
//							realNameInfo = new RealNameInfo();
//							realNameInfo.setUserId(userId);
//							realNameInfo.setRealName(realName);
//							realNameInfo.setCardNo(cardNo);
//							if(detailsJsonObj.getInteger("sex")==0){//女
//								realNameInfo.setSex(2);
//							}else{//男
//								realNameInfo.setSex(1);
//							}
//							realNameInfo.setBirth(detailsJsonObj.getString("birth"));
//							realNameInfo.setAddrCode(detailsJsonObj.getString("addrCode"));
//							realNameInfo.setCheckBit(detailsJsonObj.getString("checkBit"));
//							realNameInfo.setLength(detailsJsonObj.getInteger("length"));
//							realNameInfo.setAddr(detailsJsonObj.getString("addr"));
//							realNameInfo.setProvince(detailsJsonObj.getString("province"));
//							realNameInfo.setCity(detailsJsonObj.getString("city"));
//							realNameInfo.setArea(detailsJsonObj.getString("area"));
//							realNameInfo.setTime(new Date());
//						}else{
//							log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
//							return null;
//						}
//					}else{
//						log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
//						return null;
//					}
//				}else{//认证失败
//					return null;
//				}
//			}else{
//				log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
//				return null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
//			return null;
//		} finally {
//			if (httpURLConnection != null) {
//				httpURLConnection.disconnect();
//			}
//		}
//		return realNameInfo;
////		return null;
//	}
	
//	/**
//	 * 银行卡四元素认证主逻辑
//	 * @param userId   当前用户ID
//	 * @param bankCard 银行卡号
//	 * @param realName 银行卡对应真实姓名
//	 * @param cardNo   银行卡对应身份证号
//	 * @param phone    银行卡对应手机号
//	 * @return
//	 */
//	public static UserBankCardInfo checkBankCard(long userId, String bankCard, String realName, String cardNo, String phone) {
//		String returnStr = null; // 返回结果定义
//		URL url = null;
//		HttpURLConnection httpURLConnection = null;
//		UserBankCardInfo userBankCardInfo = null;
//		String param="key="+Constants.BANKCARD_KEY+"&bankcard="+bankCard+"&realName="+realName+"&cardNo="+cardNo+"&Mobile="+phone+"&cardtype=";
//		try {
//			url = new URL(Constants.CHECK_BANKCARD_URL);
//			httpURLConnection = (HttpURLConnection) url.openConnection();
//			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
//			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			httpURLConnection.setDoOutput(true);
//			httpURLConnection.setDoInput(true);
//			httpURLConnection.setRequestMethod("POST"); // post方式
//			httpURLConnection.connect();
//			//POST方法时使用
//			byte[] byteParam = param.getBytes("UTF-8");
//			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
//			out.write(byteParam);
//			out.flush();
//			out.close();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
//			StringBuffer buffer = new StringBuffer();
//			String line = "";
//			while ((line = reader.readLine()) != null) {
//				buffer.append(line);
//			}
//			reader.close();
//			returnStr = buffer.toString();
//			//解析json
//			JSONObject jsonObj = JSONObject.parseObject(returnStr);
//			if(jsonObj!=null){
//				int error_code = -1;
//				error_code = jsonObj.getInteger("error_code");//状态码
//				if(error_code==0){//通信正常
//					JSONObject resultJsonObj = jsonObj.getJSONObject("result");
//					if(resultJsonObj!=null){
//						String isok = resultJsonObj.getString("isok");
//						if("1".equals(isok)){//认证通过
//							userBankCardInfo = new UserBankCardInfo();
//							userBankCardInfo.setUserId(userId);
//							userBankCardInfo.setBankCard(bankCard);
//							userBankCardInfo.setBankCardNo(cardNo);
//							userBankCardInfo.setBankName(realName);
//							userBankCardInfo.setBankPhone(phone);
//							userBankCardInfo.setTime(new Date());
//						}else{
//							log.info("服务端访问“寻程银行卡四元素认证机构”失败，认证机构响应数据异常！");
//							return null;
//						}
//					}else{
//						log.info("服务端访问“寻程银行卡四元素认证机构”失败，认证机构响应数据异常！");
//						return null;
//					}
//				}else{//认证失败
//					return null;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("服务端访问“寻程银行卡四元素认证机构”失败，认证机构响应数据异常！");
//			return null;
//		} finally {
//			if (httpURLConnection != null) {
//				httpURLConnection.disconnect();
//			}
//		}
//		return userBankCardInfo;
////		return null;
//	}

//	public static void main(String[] args) {
//		RealNameUtils realNameUtils = new RealNameUtils();
//		UserBankCardInfo userBankCardInfo = realNameUtils.checkBankCard(10076,"6225768308550118","孙天杰","130982198808021971","15901213694");
//		RealNameInfo realNameInfo = realNameUtils.checkRealName(10076, "孙天杰","130982198808021971");
//		System.out.println(userBankCardInfo);
//		System.out.println(realNameInfo);
//	}
}
