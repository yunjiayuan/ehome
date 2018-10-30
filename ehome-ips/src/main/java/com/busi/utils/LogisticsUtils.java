package com.busi.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.entity.UsedDealLogistics;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SunTianJie
 * @version create time：2016-8-23 下午2:13:29
 */
@Component
public class LogisticsUtils {

    /***
     * 查询物流信息
     * @param brand 物流类型 如：sto
     * @param no    物流订单编号 402639866662
     * @return
     */
    public static UsedDealLogistics findLogisticsInfo(String brand, String no) {
        String host = "http://weikuaidi.market.alicloudapi.com";
        String path = "/aliyunapi";
        String method = "POST";
        String appcode = "2c70befbb1344cec8249083c1264fde3";//你自己的AppCode
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("brand", brand);
        bodys.put("no", no);
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            String resData = "";
//	    	System.out.println(response.toString());
            //获取response的body
            if (response.getEntity() != null) {
                resData = EntityUtils.toString(response.getEntity());
            }
            if (!CommonUtils.checkFull(resData)) {
                resData = resData.substring(1, resData.length() - 1);
                if (!CommonUtils.checkFull(resData)) {
                    Map<String, Object> jb = new HashMap<>();
                    jb = CommonUtils.objectToMap(resData);
                    UsedDealLogistics logisticsInfo = new UsedDealLogistics();
                    logisticsInfo.setNo(jb.get("no").toString());
                    logisticsInfo.setOrders(jb.get("order").toString());
                    logisticsInfo.setStatus(jb.get("status").toString());
                    String ds = jb.get("data").toString();
                    JSONArray array = new JSONArray();
                    List<String> list = new ArrayList<String>();
                    if (!CommonUtils.checkFull(ds)) {
                        list.add(ds);
                        array.add(list);
                    }
                    String data = "";
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            Map<String, Object> jb2 = new HashMap<>();
                            jb2 = CommonUtils.objectToMap(array.getString(i));
                            String content = jb2.get("time").toString() + "&&" + jb2.get("context").toString();
                            if (i == 0) {
                                data = content;
                            } else {
                                data += "##" + content;
                            }
                        }
                    }
                    logisticsInfo.setData(data);
                    return logisticsInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//	public static void main(String[] args) {
//	    findLogisticsInfo("sto", "402639866662");
//	}
}
