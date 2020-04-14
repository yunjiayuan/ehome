package com.busi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationImage;
import com.busi.entity.EpidemicSituationTianqi;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** 
 * 疫情工具类
 *
 * @author SunTianJie 
 *
 * @version create time：2017-6-5 下午1:37:14 
 * 
 */
@Slf4j
public class EpidemicSituationUtils {
	
	/****
	 * 调用第三方疫情接口主逻辑
	 * @return
	 */
	public static EpidemicSituation getEpidemicSituation(){

		String returnStr = null; // 返回结果定义
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		try {
			url = new URL(Constants.EPIDEMIC_SITUATION_URL);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
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
				error_code = jsonObj.getInteger("error");//状态码
				if(error_code==0){//成功
					JSONObject data = jsonObj.getJSONObject("data");
					//获取数据概要对象
					JSONObject statisticsJosnObject = data.getJSONObject("statistics");
					EpidemicSituation statistics = JSON.toJavaObject(statisticsJosnObject,EpidemicSituation.class);
					if(statistics!=null){
						EpidemicSituationImage[] quanguoarray = statistics.getQuanguoTrendChart();
						if(quanguoarray!=null){
							String quanguoTrendCharts = "";
							for (int i = 0; i <quanguoarray.length ; i++) {
								String quanguoData = quanguoarray[i].getImgUrl()+","+quanguoarray[i].getTitle();
								if(i!=quanguoarray.length-1){
									quanguoTrendCharts  += quanguoData+";";
								}else{
									quanguoTrendCharts  += quanguoData;
								}
							}
							statistics.setQuanguoTrendCharts(quanguoTrendCharts);
						}
						EpidemicSituationImage[] hbFeiHbTrendChart= statistics.getHbFeiHbTrendChart();
						if(hbFeiHbTrendChart!=null){
							String hbFeiHbTrendCharts = "";
							for (int i = 0; i <hbFeiHbTrendChart.length ; i++) {
								String hbFeiHbData = hbFeiHbTrendChart[i].getImgUrl()+","+hbFeiHbTrendChart[i].getTitle();
								if(i!=hbFeiHbTrendChart.length-1){
									hbFeiHbTrendCharts  += hbFeiHbData+";";
								}else{
									hbFeiHbTrendCharts  += hbFeiHbData;
								}
							}
							statistics.setHbFeiHbTrendCharts(hbFeiHbTrendCharts);
						}
						statistics.setListByArea(data.getJSONArray("listByArea").toString());//国内各省市数据
						statistics.setListByOther(data.getJSONArray("listByOther").toString());//国外数据
						return statistics;
					}
				}else{//失败
					//打印错误信息
					log.info("第三方疫情平台状态码异常："+error_code);
					return null;
				}
			}else{
				//打印错误信息
				log.info("第三方疫情平台响应数据格式有误！");
				return null;
			}
		} catch (Exception e) {
			log.info("解析第三方疫情平台数据异常！");
			e.printStackTrace();
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return null;
	}
	/****
	 * 调用天气平台第三方疫情接口主逻辑
	 * @return
	 */
	public static EpidemicSituationTianqi getEpidemicSituationByTianqi(){

		String returnStr = null; // 返回结果定义
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		try {
			url = new URL(Constants.EPIDEMIC_SITUATION_TIANQI_URL);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
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
				error_code = jsonObj.getInteger("errcode");//状态码
				if(error_code==0){//成功
					JSONObject data = jsonObj.getJSONObject("data");
					//获取数据概要对象
					EpidemicSituationTianqi epidemicSituationTianqi = JSON.toJavaObject(data,EpidemicSituationTianqi.class);
					return epidemicSituationTianqi;
				}else{//失败
					//打印错误信息
					log.info("第三方疫情平台状态码异常："+error_code);
					return null;
				}
			}else{
				//打印错误信息
				log.info("第三方疫情平台响应数据格式有误！");
				return null;
			}
		} catch (Exception e) {
			log.info("解析第三方疫情平台数据异常,第三方平台数据返回异常！");
			e.printStackTrace();
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	/****
	 * 调用第三方疫情接口主逻辑（天行数据）
	 * @return
	 */
	public static EpidemicSituation getEpidemicSituationtianXing(){

		String epidemicSituationUrl = Constants.EPIDEMIC_SITUATION_TIANXING_URL;
		String returnStr = null; // 返回结果定义
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		try {
			url = new URL(epidemicSituationUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
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
				int code = -1;
				String reason = "";
				code = jsonObj.getInteger("code");//状态码
				if(code==200){//成功
					JSONArray newslist = jsonObj.getJSONArray("newslist");
					JSONObject jsonObject = (JSONObject) newslist.get(0);
					JSONObject desc = jsonObject.getJSONObject("desc");
					//获取数据概要对象
					EpidemicSituation epidemicSituation = JSON.toJavaObject(desc,EpidemicSituation.class);
					return epidemicSituation;
				}else{//失败
					//打印错误信息
					return null;
				}
			}else{
				//打印错误信息
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}
}
