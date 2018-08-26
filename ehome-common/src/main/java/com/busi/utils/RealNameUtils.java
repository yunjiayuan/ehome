package com.busi.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
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
	
//	private static String check_realname_url="https://v.apistore.cn/api/a1";//实名认证地址
//
//	private static String check_bankCard_url="https://v.apistore.cn/api/v4/verifybankcard4";//银行卡四元素认证地址
	
//	private String realname_key="01744161e13b2b7824c40abb530fe207";//实名认证key
//	
//	private String bankCard_key="4a3d1d6e4d0ffa8e1965ba7560a515c1";////银行卡四元素认证key
	
//	private static String realname_key="381b395c0c9dfa8e06335ef948ffcba3";//实名认证key
//
//	private static String bankCard_key="e56266e81c79dd76edb09e8c111d3ced";////银行卡四元素认证key
	
	/****
	 * 实名认证主逻辑
	 * @param userId
	 * @param realName
	 * @param cardNo
	 * @return
	 */
	public static RealNameInfo checkRealName(long userId, String realName, String cardNo){
		RealNameInfo realNameInfo = null;
		String param="key="+Constants.REALNAME_KEY+"&cardNo="+cardNo+"&realName="+realName+"&information=1";
		String returnStr = null; // 返回结果定义
		URL url = null;
		HttpURLConnection httpURLConnection = null;

		try {
			url = new URL(Constants.CHECK_REALNAME_URL);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST"); // post方式
			httpURLConnection.connect();
			//POST方法时使用
			byte[] byteParam = param.getBytes("UTF-8");
			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
			out.write(byteParam);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			returnStr = buffer.toString();
			//解析json
			JSONObject jsonObj = JSONObject.parseObject(returnStr);
			if(jsonObj!=null){
				int error_code = -1;
				String reason = "";
				error_code = jsonObj.getInteger("error_code");//状态码
				reason = jsonObj.getString("reason");//状态码
				if(error_code==0&&"认证通过".equals(reason)){//认证通过
					JSONObject resultJsonObj = jsonObj.getJSONObject("result");
					if(resultJsonObj!=null){
						JSONObject detailsJsonObj = resultJsonObj.getJSONObject("details");
						if(detailsJsonObj!=null){
							realNameInfo = new RealNameInfo();
							realNameInfo.setUserId(userId);
							realNameInfo.setRealName(realName);
							realNameInfo.setCardNo(cardNo);
							if(detailsJsonObj.getInteger("sex")==0){//女
								realNameInfo.setSex(2);
							}else{//男
								realNameInfo.setSex(1);
							}
							realNameInfo.setBirth(detailsJsonObj.getString("birth"));
							realNameInfo.setAddrCode(detailsJsonObj.getString("addrCode"));
							realNameInfo.setCheckBit(detailsJsonObj.getString("checkBit"));
							realNameInfo.setLength(detailsJsonObj.getInteger("length"));
							realNameInfo.setAddr(detailsJsonObj.getString("addr"));
							realNameInfo.setProvince(detailsJsonObj.getString("province"));
							realNameInfo.setCity(detailsJsonObj.getString("city"));
							realNameInfo.setArea(detailsJsonObj.getString("area"));
							realNameInfo.setTime(new Date());
						}else{
							log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
							return null;
						}
					}else{
						log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
						return null;
					}
				}else{//认证失败
					return null;
				}
			}else{
				log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("服务端访问“寻程实名认证机构”失败，认证机构响应数据异常！");
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return realNameInfo;
//		return null;
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
		String returnStr = null; // 返回结果定义
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		UserBankCardInfo userBankCardInfo = null;
		String param="key="+Constants.BANKCARD_KEY+"&bankcard="+bankCard+"&realName="+realName+"&cardNo="+cardNo+"&Mobile="+phone+"&cardtype=";
		try {
			url = new URL(Constants.CHECK_BANKCARD_URL);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST"); // post方式
			httpURLConnection.connect();
			//POST方法时使用
			byte[] byteParam = param.getBytes("UTF-8");
			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
			out.write(byteParam);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			returnStr = buffer.toString();
			//解析json
			JSONObject jsonObj = JSONObject.parseObject(returnStr);
			if(jsonObj!=null){
				int error_code = -1;
				error_code = jsonObj.getInteger("error_code");//状态码
				if(error_code==0){//通信正常
					JSONObject resultJsonObj = jsonObj.getJSONObject("result");
					if(resultJsonObj!=null){
						String isok = resultJsonObj.getString("isok");
						if("1".equals(isok)){//认证通过
							userBankCardInfo = new UserBankCardInfo();
							userBankCardInfo.setUserId(userId);
							userBankCardInfo.setBankCard(bankCard);
							userBankCardInfo.setBankCardNo(cardNo);
							userBankCardInfo.setBankName(realName);
							userBankCardInfo.setBankPhone(phone);
							userBankCardInfo.setTime(new Date());
						}else{
							log.info("服务端访问“寻程银行卡四元素认证机构”失败，认证机构响应数据异常！");
							return null;
						}
					}else{
						log.info("服务端访问“寻程银行卡四元素认证机构”失败，认证机构响应数据异常！");
						return null;
					}
				}else{//认证失败
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("服务端访问“寻程银行卡四元素认证机构”失败，认证机构响应数据异常！");
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return userBankCardInfo;
//		return null;
	}

//	public static void main(String[] args) {
//		RealNameUtils realNameUtils = new RealNameUtils();
//		UserBankCardInfo userBankCardInfo = realNameUtils.checkBankCard(10076,"6225768308550118","孙天杰","130982198808021971","15901213694");
//		RealNameInfo realNameInfo = realNameUtils.checkRealName(10076, "孙天杰","130982198808021971");
//		System.out.println(userBankCardInfo);
//		System.out.println(realNameInfo);
//	}
}
