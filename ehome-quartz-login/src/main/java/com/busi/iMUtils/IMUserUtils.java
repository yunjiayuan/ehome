package com.busi.iMUtils;

import com.alibaba.fastjson.JSONObject;
import com.busi.entity.UserInfo;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @program: ehome
 * @description: IM即时通讯
 * @author: ZHaoJiaJie
 * @create: 2019-01-17 14:18
 */
public class IMUserUtils {

    /***
     * 向环信用户发送消息  一对一
     * @param message      消息内容
     * @param sendUser     发送者
     * @param receiveUsers 接收消息者
     * @param token        环信token
     * @param type         消息类型 0文本（默认） 1图片 2音频 3小视频 4文件
     */
    public static void sendMessageToIMUser(String message, UserInfo sendUser, UserInfo receiveUsers, String token, int type) {
        if (type == 1) {

        } else if (type == 2) {

        } else if (type == 3) {

        } else if (type == 4) {

        } else {
            String sendUrlPath = "https://a1.easemob.com/yunjiayuan/ehome/messages";
            int statusCode = 0;
            JSONObject jo = new JSONObject();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut method = new HttpPut(sendUrlPath);
            jo.put("target_type", "users");
            jo.put("target", "[" + receiveUsers.getProType() + "_" + receiveUsers.getHouseNumber() + "]");
            jo.put("msg", "{'type' : 'txt','msg' : '" + message + "'}");
            jo.put("from", sendUser.getProType() + "_" + sendUser.getHouseNumber());
            jo.put("ext", "{" +
                    "'user_id'='" + sendUser.getUserId() + "';" +
                    "'user_name_from'='" + sendUser.getName() + "';" +
                    "'user_head_from'='http://res.lichengwang.com/" + sendUser.getHead() + "';" +
                    "'user_id_to'='" + receiveUsers.getUserId() + "';" +
                    "'user_name_to'='" + receiveUsers.getName() + "';" +
                    "'user_head_to'='http://res.lichengwang.com/" + receiveUsers.getHead() + "';" +
                    "}");
            StringEntity entity = new StringEntity(jo.toString(), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
            method.setHeader("Authorization", "Bearer " + token);
            try {
                HttpResponse result = httpClient.execute(method);
                statusCode = result.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    // 请求结束，返回结果
                    EntityUtils.toString(result.getEntity());
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
