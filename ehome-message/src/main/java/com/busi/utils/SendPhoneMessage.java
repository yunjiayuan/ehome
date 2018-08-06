package com.busi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import lombok.extern.slf4j.Slf4j;

/** 
 * 发送短信消息
 * 
 * @author SunTianJie 
 *
 * @version create time：2015-7-13 下午1:51:11 
 * 
 */
@Slf4j
public class SendPhoneMessage{

	private String content;//短信内容
	
	private String phones;//手机队列 格式：15988888888 或 15977777777,1596666666
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}
	
	public void sendPhoneMessage(){
		URLConnection urlConnection = null;
		URL url = null;
		BufferedReader br =  null;
		String sendUrlPath = Constants.SENDURLPATH;
		String action = Constants.ACTION;
		String ac = Constants.AC;//用户账号
		String authkey = Constants.AUTHKEY;//认证密钥
		String cgid = Constants.CGID;//通道组编号
		String csid = Constants.CSID;//签名编号
		String c = "";
		try {
			if(CommonUtils.checkFull(content)){
				log.info("MQ短信平台向手机号：["+phones+"]发送短信验证信息失败，短信内容为空，终止发送！");
				return;
			}
			c = URLEncoder.encode(content,"UTF-8");
			StringBuilder sb = new StringBuilder();
			url = new URL(sendUrlPath+"action="+action+"&ac="+ac+"&authkey="+authkey+"&cgid="+cgid+"&csid="+csid+"&c="+c+"&m="+phones);// 生成url对象
			urlConnection = url.openConnection();// 打开url连接
			urlConnection.setConnectTimeout(30000);//设置连接超时
			urlConnection.connect();//连接
			br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			String result = sb.toString().trim();
			String resultStuts = "";
			if(!CommonUtils.checkFull(result)){
				resultStuts = result.substring(result.indexOf("result=")+8,result.indexOf("result=")+9);
				if(!CommonUtils.checkFull(resultStuts)&&"1".equals(resultStuts)){
					log.info("MQ短信平台向手机号：["+phones+"]发送短信验证信息成功!");
				}else{
					log.info("MQ短信平台向手机号：["+phones+"]发送短信验证信息失败,MQ短信平台操作失败，请及时联系管理员!");
				}
			}else{
				log.info("MQ短信平台向手机号：["+phones+"]发送短信验证信息失败,返回数据为空!");
			}
		} catch (MalformedURLException e) {
			log.error("不能连接到MQ短信平台URL："+sendUrlPath );
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			log.error("服务器转换消息内容时出现异常：content"+content );
			e.printStackTrace();
		}catch (IOException e) {
			log.error("连接到短信平台URL抛出异常信息："+sendUrlPath );
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
