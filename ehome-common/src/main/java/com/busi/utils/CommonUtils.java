package com.busi.utils;


import com.busi.qiniu.util.Auth;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本类主要提供一些常用方法
 * @author suntj
 *
 * 2013-1-16
 */
public class CommonUtils {

	/***
	 * 检查字符串是否为空, 为空则返回true 反之返回false
	 * @param param
	 * @return
	 */
	public static boolean checkFull(String param) {
		return ((null == param || "".equals(param.trim()) || "null"
				.equals(param.trim())) ? true : false);
	}
	/***
	 * byte数组转换成int
	 * @param b
	 * @return
	 */
	public static int bytesToInt(byte[] b) {
		// byte[] b=new byte[]{1,2,3,4};
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		int icnt = 4 > b.length ? b.length : 4;

		for (int i = 0; i < icnt; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}
	/***
	 * byte数组转换成long
	 * @param b
	 * @return
	 */
	public static long bytesToLong(byte[] b) {
		// byte[] b=new byte[]{1,2,3,4};
		int mask = 0xff;
		int temp = 0;
		long res = 0;
		int icnt = 8 > b.length ? b.length : 8;
		for (int i = 0; i < icnt; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}
	/***
	 * int转换成byte数组
	 * @param
	 * @return
	 */
	public static byte[] intToBytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}
	/***
	 * int转换成byte数组
	 * @param
	 * @return
	 */
	public static byte[] longToBytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		return b;
	}
	/***
	 * 将字符串转换成MD5码
	 * type 加密位数 16 32 
	 * @return
	 */
	public static String strToMD5(String code,int type){
		String md5Code = null;
		if(!checkFull(code)){
			md5Code = bytesToMD5(code.getBytes(),type);
		}
		return md5Code;
	}
	/**
	 * 把字节数组转成16进位制数
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes,int type) {
		StringBuffer md5str = new StringBuffer();
		//把数组每一字节换成16进制连成md5字符串
		int digital;
		for (int i = 0; i < bytes.length; i++) {
			 digital = bytes[i];
			if(digital < 0) {
				digital += 256;
			}
			if(digital < 16){
				md5str.append("0");
			}
			md5str.append(Integer.toHexString(digital));
			
		}
		if(type==16){
			return md5str.toString().substring(8,24);
		}else{//默认32				
			return md5str.toString();
		}
	}
	/**
	 * 把字节数组转换成md5
	 * @param input 将要转换的字节数组
	 * @param type 进制数 16（默认） 32 64
	 * @return
	 */
	public static String bytesToMD5(byte[] input,int type) {
		String md5str = null;
		try {
			//创建一个提供信息摘要算法的对象，初始化为md5算法对象
			MessageDigest md = MessageDigest.getInstance("MD5");
			//计算后获得字节数组
			byte[] buff = md.digest(input);
			md5str = bytesToHex(buff,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5str;
	}
	/***
	 * 获取访问用户的IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = "";
		if(request!=null){			
			ip = request.getHeader("x-forwarded-for");
			if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		}
        return ip;
    }
	/***
	 *
	 * @param length 生成随机数位数
	 * @param type 0 数字字母混排（默认） 1纯数字
	 * @return
	 */
	public static String getRandom(int length,int type){
		String randomCode = "";
		Random random = new Random();
		if(type==1){//纯数字
			for(int i=0;i<length;i++){
				randomCode+=random.nextInt(10);
			}
		}else{//数字字母混排
			for (int i = 0; i < length; i++) {
				// 输出字母还是数字
				String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
				// 字符串
				if ("char".equalsIgnoreCase(charOrNum)) {
					// 取得大写字母还是小写字母
					//int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;//随机生成大小写字母
					//随机生成小写字母
					int choice = 97;
					randomCode += (char) (choice + random.nextInt(26));
				} else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
					randomCode += String.valueOf(random.nextInt(10));
				}
			}
		}
		return randomCode;
	}
	
	/** 
	 * 手机端 使用输出流    liu 2014年11月4日 17:51:59
	 */ 
	public static void useWriter(PrintWriter out,String ems){ 
		out.write(ems); 
		out.flush(); 
		out.close(); 
	}
	
	/***
	 * 根据字符串中包含的中文、数字、英文计算相应字符串的字节长度 suntj 20150507
	 * @param  value 将要处理的字符串
	 * @return res   返回字符串的字节长度
	 */
	public static int getStringLengsByByte(String value) {

		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}
	/***
	 * 计算弧长
	 * @param d
	 * @return
	 */
	private static double rad(double d){
		return d * Math.PI / 180.0;     //计算弧长
	}
	/***
	 *  计算两个坐标点间的距离
	 * @param longitude1  坐标点1的经度
	 * @param latitude1    坐标点1的纬度
	 * @param longitude2  坐标点2的经度
	 * @param latitude2    坐标点2的纬度
	 * @return 两点间的距离 单位:米
	 */
	public static double getShortestDistance(double longitude1,double latitude1,double longitude2,double latitude2 ){
		double radLat1 = rad(latitude1);
		double radLat2 = rad(latitude2);
		double a = radLat1 - radLat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		s = s * 6378.137;//乘以地球半径
		s = s * 1000;    //换算成米
		return s;

	} 
	/**  
     * 生成以中心点为中心的四方形经纬度  
     *   
     * @param lat 纬度  
     * @param lon 精度  
     * @param raidus 半径（以米为单位）  
     * @return  
     */    
    public static double[] getAround(double lat, double lon, int raidus) {    
    
        Double latitude = lat;    
        Double longitude = lon;    
    
        Double degree = (24901 * 1609) / 360.0;    
        double raidusMile = raidus;    
    
        Double dpmLat = 1 / degree;    
        Double radiusLat = dpmLat * raidusMile;    
        Double minLat = latitude - radiusLat;    
        Double maxLat = latitude + radiusLat;    
    
        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));    
        Double dpmLng = 1 / mpdLng;                 
        Double radiusLng = dpmLng * raidusMile;     
        Double minLng = longitude - radiusLng;      
        Double maxLng = longitude + radiusLng;   
        return new double[] { minLat, minLng, maxLat, maxLng };   
//        return new double[] { Math.round(minLat*1000000)/1000000.0,Math.round(minLng*1000000)/1000000.0 ,
//        		Math.round(maxLat*1000000)/1000000.0,Math.round(maxLng*1000000)/1000000.0};    
    }  
    /***
	 * 过滤数组中重复的元素
	 * @param key
	 * @return 返回过滤后的数组
	 */
	public static String[] distinctArray(String[] key){
		HashMap<String, String> keyToMap = new HashMap<String, String>();
		for(int i=0;i<key.length;i++){
			keyToMap.put(key[i], key[i]);
		}
		return (String[]) keyToMap.keySet().toArray(new String[keyToMap.keySet().size()]);
	}
	/**
	 * 手机号格式验证
	 * 
	 * @param  str
	 * @return 验证通过返回true
	 */
	public static boolean checkPhone(String str) { 
		Pattern p = null;
		Matcher m = null;
		boolean b = false; 
		p = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches(); 
		return b;
	}
	/**
	 * 电话号码验证
	 * 
	 * @param  str
	 * @return 验证通过返回true
	 */
	public static boolean checkTelephone(String str) { 
		Pattern p1 = null,p2 = null;
		Matcher m = null;
		boolean b = false;  
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
		if(str.length() >9)
		{	m = p1.matcher(str);
 		    b = m.matches();  
		}else{
			m = p2.matcher(str);
 			b = m.matches(); 
		}  
		return b;
	}
	/**
	 * 邮箱验证格式
	 * 
	 * @param  email
	 * @return 验证通过返回true
	 */
	public static boolean checkEmail(String email) { 
		 String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";    
		 Pattern regex = Pattern.compile(check);    
		 Matcher matcher = regex.matcher(email);    
		 boolean isMatched = matcher.matches();    
		 return isMatched; 
	}
	/***
	 * 根据出生日期 返回年龄 
	 * @param time yyyy-MM-dd 
	 * @return
	 */
	public static int getAge(String time){

		int returnAge=0;
		String strBirthdayArr[]=time.split("-");
		 int birthYear = Integer.valueOf(strBirthdayArr[0]);
		 int birthMonth = Integer.valueOf(strBirthdayArr[1]);
		 int birthDay =  Integer.valueOf(strBirthdayArr[2]);
		 Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		 int nowYear = c.get(Calendar.YEAR); 
		 int nowMonth = c.get(Calendar.MONTH)+1; 
		 int nowDay = c.get(Calendar.DATE); 
		 
		  if(nowYear == birthYear)
		    {
		        returnAge = 0;//同年 则为0岁
		    }else{
		    	
		        int ageDiff = nowYear - birthYear ; //年之差
		        if(ageDiff > 0)
		        {
		            if(nowMonth == birthMonth)
		            {
		            	int dayDiff = nowDay - birthDay;//日之差
		                if(dayDiff < 0)
		                {
		                    returnAge = ageDiff - 1;
		                }
		                else
		                {
		                    returnAge = ageDiff ;
		                }
		            }
		            else
		            {
		            	int monthDiff = nowMonth - birthMonth;//月之差
		                if(monthDiff < 0)
		                {
		                    returnAge = ageDiff - 1;
		                }
		                else
		                {
		                    returnAge = ageDiff ;
		                }
		            }
		        }
		        else
		        {
		            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
		        }
		    
		    }
		return returnAge;
	}
	
	
	public static String getPasswordBySalt(String password, String salt){
		
		//存放密码与随机数拼接的字符串
		StringBuffer buff = new StringBuffer();
		//记录随机码下标
		int sta_i = 0;
		for (int i = 0; i < password.length(); i++) {
			//算出密码基数位子 且 随机码下标小于随机码字符长度
			if(i%2==0 && sta_i < 6){
				//在密码基数位子插入随机码下标指向的当前字符
				buff.append(password.charAt(i)+""+salt.charAt(sta_i)) ;
				//随机码下标++
				sta_i++;
			}else{
				//偶数位保持原样
				buff.append(password.charAt(i));
			}
		}
		//再次MD5
		return CommonUtils.strToMD5(buff.toString(), 32);
	}
	
	/**
	 * 检查数字位数, 符合返回true 反之返回false
	 * @param num  数字
	 * @param intLen 整数位长度
	 * @param decLen 小数位长度
	 * @return 符合返回true 
	 */
	public static boolean checkDecimal(double num,int intLen,int decLen){

		String str = "^(-)?[0-9]{1,"+intLen+"}+(.[0-9]{1,"+decLen+"})?$";//匹配（正负）整数x位，小数x位的正则表达式

		if(String.valueOf(num).matches(str)){
			//通过验证
			return true;	
		}
		return false;
	}
	/**  
     * 匹配中国邮政编码  
     * @param  "postcode" 邮政编码
     * @return 验证成功返回true，验证失败返回false  
     */   
    public static boolean isPostCode(String postCode){  
        String reg = "\\d{6}";  
        return Pattern.matches(reg, postCode);  
    }  
    /**
     * 判断身份证格式
     * 
     * @param "idNum"
     * @return
     */
    public static boolean isIdCard(String idCard) {

        // 中国公民身份证格式：长度为15或18位，最后一位可以为字母
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
        // 格式验证
        if (!idNumPattern.matcher(idCard).matches())
        	return false;
        // 合法性验证
        int year = 0;
        int month = 0;
        int day = 0;
        if (idCard.length() == 15) {
            // 一代身份证
            // 提取身份证上的前6位以及出生年月日
            Pattern birthDatePattern = Pattern.compile("\\d{6}(\\d{2})(\\d{2})(\\d{2}).*");
            Matcher birthDateMather = birthDatePattern.matcher(idCard);
            if (birthDateMather.find()) {
                year = Integer.valueOf("19" + birthDateMather.group(1));
                month = Integer.valueOf(birthDateMather.group(2));
                day = Integer.valueOf(birthDateMather.group(3));
            }
        } else if (idCard.length() == 18) {
            // 二代身份证
            // 提取身份证上的前6位以及出生年月日
            Pattern birthDatePattern = Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");
            Matcher birthDateMather = birthDatePattern.matcher(idCard);
            if (birthDateMather.find()) {
                year = Integer.valueOf(birthDateMather.group(1));
                month = Integer.valueOf(birthDateMather.group(2));
                day = Integer.valueOf(birthDateMather.group(3));
            }
        }
        // 年份判断，100年前至今
        Calendar cal = Calendar.getInstance();
        // 当前年份
        int currentYear = cal.get(Calendar.YEAR);
        if (year <= currentYear - 100 || year > currentYear)
            return false;
        // 月份判断
        if (month < 1 || month > 12)
            return false;
        // 日期判断
        // 计算月份天数
        int dayCount = 31;
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            dayCount = 31;
            break;
        case 2:
            // 2月份判断是否为闰年
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                dayCount = 29;
                break;
            } else {
                dayCount = 28;
                break;
            }
        case 4:
        case 6:
        case 9:
        case 11:
            dayCount = 30;
            break;
        }
        if (day < 1 || day > dayCount)
            return false;
        return true;
    }
    
    /** 
     * 校验银行卡卡号 
     */  
    public static boolean checkBankCard(String bankCard) {  
             if(bankCard.length() < 15 || bankCard.length() > 19) {
                 return false;
             }
             char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));  
             if(bit == 'N'){  
                 return false;  
             }  
             return bankCard.charAt(bankCard.length() - 1) == bit;  
    }  

    /** 
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 
     * @param nonCheckCodeBankCard 
     * @return 
     */  
    public static char getBankCardCheckCode(String nonCheckCodeBankCard){  
        if(nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0  
                || !nonCheckCodeBankCard.matches("\\d+")) {  
            //如果传的不是数据返回N  
            return 'N';  
        }  
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();  
        int luhmSum = 0;  
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {  
            int k = chs[i] - '0';  
            if(j % 2 == 0) {  
                k *= 2;  
                k = k / 10 + k % 10;  
            }  
            luhmSum += k;             
        }  
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');  
    }
    /***
     * 根据省份证号计算年龄（周岁）
     * @param IdNO
     * @return
     */
    public static int getAgeByIdCard(String IdNO){
        int leh = IdNO.length();
        String dates="";
        if (leh == 18) {
            dates = IdNO.substring(6, 10);
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year=df.format(new Date());
            int u=Integer.parseInt(year)-Integer.parseInt(dates);
            return u;
        }else{
            dates = IdNO.substring(6, 8);
            return Integer.parseInt(dates);
        }

    }
    /***
     * 根据省份证号计算性别  1男2女
     * @param IdNO
     * @return
     */
    public static int getSexByIdCard(String IdNO){
        int leh = IdNO.length();
        String dates="";
        if (leh == 18) {
        	dates = IdNO.substring(16, 17);
        }else{
        	dates = IdNO.substring(14, 15);
        }
        int sex = 0;//1男2女
        if(Integer.parseInt(dates)%2==0){
        	sex = 2;//2女
        }else{
        	sex = 1;//1男
        }
        return sex;

    }

	/***
	 * 服务端返回客户端数据格式封装工具
	 * @param obj         返回数据对象
	 * @param statusCode  状态码
	 * @return
	 */
    public static Map<String, Object> returnDataFormat( int statusCode,Object obj){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statusCode", statusCode);
		if(obj!=null){
			map.put("data", obj);
		}else{
			map.put("data", "[]");
		}
		return map;
	}

	/***
	 * 检测省市区参数是否正确
	 * @param country   国家ID 目前只有中国0
	 * @param province  省ID
	 * @param city      市ID
	 * @param district  区ID
	 * @return
	 */
	public static boolean checkProvince_city_district(int country, int province,int city ,int district){
    	boolean flag = true;
		if(country != 0){
			return false;
		}
		if(province<0||province>33){
			return false;
		}

		int key = 0,city_key=0;
		Map<String, Integer> dsy_city =	new HashMap<String,Integer>();
		Map<String, Integer> dsy_district =	new HashMap<String,Integer>();
		String [] _city = Constants.DSY_CITY.split(",");
		String [] _district = Constants.DSY_DISTRICT.split(",");
		for (int i = 0; i < Constants.DSY_PROVINCE; i++) {

			city_key = Integer.valueOf(_city[i]);
			//表示每个省下有几个市
			dsy_city.put("0"+i, city_key);

			for (int j = 0; j < city_key; j++) {
				//表示每个市下有几个区
				dsy_district.put("0"+i+""+j, Integer.valueOf(_district[key]));
				key++;
			}
		}
		if(city<0||city>dsy_city.get(country+""+province) ){
			return false;
		}
		if(district<0||district>dsy_district.get(country+""+province+""+city)){
			return false;
		}
    	return flag;
	}
	/**
	 * 生成门牌号
	 * @return
	 */
	public static long getHouseNumber(int proId,RedisUtils redisUtils){
		long houseNumber = 0L;
		boolean b = true;
		while(b){
			houseNumber = redisUtils.hashIncr("houseNumberCount",proId+"",1);//原子操作 递增1
			//判断生成的号码是否是靓号
			boolean falg = false;
			//判断是否是预留靓号
			falg =	isPretty(houseNumber);
			if(falg){//是预留靓号
				continue;
			}
			//判断是否在预选账号中
			Object o = redisUtils.hget("pickNumberMap",proId+"");
			if(o!=null){
				continue;//已存在
			}
			b = false;
		}
		return houseNumber;
	}
	/**
	 * 判断是否是靓号
	 * @param houseNumber
	 * @return
	 */
	public static boolean isPretty(long houseNumber){
		boolean flag = false;
		for(int i=0;i<Constants.PRETTY_NUMBER_ARRAY.length;i++){
			Pattern p=Pattern.compile(Constants.PRETTY_NUMBER_ARRAY[i]);
			Matcher m=p.matcher(String.valueOf(houseNumber));
			if(m.find()){
				flag = true;
				break;
			}
		}
		return flag;
	}

	/***
	 * 对象转map
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> objectToMap(Object obj){
		Map<String, Object> map = new HashMap();
		try{
			if(obj == null){
				return null;
			}
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				if(field.get(obj) instanceof Date){//判断是否为时间格式
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String date = sdf.format(field.get(obj));
					map.put(field.getName(), date);
				}else{
					map.put(field.getName(), field.get(obj));
				}
			}
			return map;
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 已登录用户获取，当前登录用户的myId
	 * @return
	 */
	public static long getMyId(){
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		return Long.parseLong(request.getHeader("myId"));
	}

	/**
	 * 已登录用户获取，当前登录用户的token
	 * @return
	 */
	public static String getToken(){
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		return request.getHeader("token");
	}

	/**
	 * 已登录用户获取，当前登录用户的myId
	 * @return
	 */
	public static String getClientId(){
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		return request.getHeader("clientId");
	}

	/**
	 * 获取七牛的token
	 * @return
	 */
	public static String getQiniuToken(){
		Auth auth = Auth.create(Constants.QINIU_ACCESSKEY,Constants.QINIU_SECRETKEY);
		return auth.uploadToken(Constants.QINIU_BUCKET,null,Constants.USER_TIME_OUT,null);
	}

	/**
	 * 获取当前时间到今天晚上12点的毫秒差
	 * @return
	 */
	public static long getCurrentTimeTo_12(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		Date d12 = c.getTime();
		long second = (d12.getTime()-new Date().getTime())/1000;
		return second;
	}

	/**
	 * 获取订单编号（通用）
	 * @param userId    当前下单的用户ID
	 * @param orderType 订单类别对应常量值
	 * @return
	 */
	public static String getOrderNumber(long userId,String orderType){
		return CommonUtils.strToMD5(orderType+userId+new Date().getTime()+CommonUtils.getRandom(6,0),16);
	}
//	public static void main(String[] args) {
////		System.out.println(checkBankCard("6225768308550119"));
//		System.out.println(checkPhone("15901213694"));
//	}
}
