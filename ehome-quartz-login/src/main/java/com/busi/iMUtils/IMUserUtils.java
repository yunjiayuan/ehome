package com.busi.iMUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.busi.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
@Slf4j
public class IMUserUtils {

    /***
     * 获取APP IM及时通讯的token
     * @return
     */
    public static IMTokenCacheBean  getToken(){
        String sendUrlPath = "https://a1.easemob.com/yunjiayuan/ehome/token";
        JSONObject jo = new JSONObject();
        int statusCode = 0;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost method = new HttpPost(sendUrlPath);
            jo.put("grant_type", "client_credentials");
            jo.put("client_id", "YXA683qssGg0Eea61DEiNmmYgg");
            jo.put("client_secret", "YXA6kTYymYOTiUp1LZ7s8MpY709uCMM");
            StringEntity entity = new StringEntity(jo.toString(),"utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
            HttpResponse result = httpClient.execute(method);
            statusCode = result.getStatusLine().getStatusCode();
            if(statusCode==HttpStatus.SC_OK){
                // 请求结束，返回结果
                String resData = EntityUtils.toString(result.getEntity());
//                JSONObject jb = JSONObject.fromObject(resData);
                JSONObject jb = JSON.parseObject(resData);
                IMTokenCacheBean imToken = new IMTokenCacheBean();
                imToken.setAccess_token(jb.getString("access_token"));
//                imToken.setApplication(jb.getString("application"));
//                imToken.setExpires_in(jb.getInt("expires_in"));
                return imToken;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 	 * 向环信用户发送消息  一对一
     * @param message      消息内容
     * @param sendUser     发送者
     * @param receiveUsers 接收消息者
     * @param token        环信token
     * @param type         消息类型 0文本（默认） 1图片 2音频 3小视频 4文件
     */
    public static void sendMessageToIMUser(String message,UserInfo sendUser,UserInfo receiveUsers,String token,int type){
        if(type==1){

        }else if(type==2){

        }else if(type==3){

        }else if(type==4){

        }else{
            String sendUrlPath = "https://a1.easemob.com/yunjiayuan/ehome/messages";
            int statusCode = 0;
            JSONObject jo = new JSONObject();
            JSONObject jo2 = new JSONObject();
            JSONObject jo3 = new JSONObject();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(sendUrlPath);
            jo.put("target_type", "users");
            Object[] userArray = {receiveUsers.getUserId()};//接收用户数组
            jo.put("target", userArray);

            jo3.put("type","txt");
            jo3.put("msg",message);
            jo.put("msg", jo3);

            jo.put("from", sendUser.getUserId()+"");//发送消息用户

            jo2.put("user_id",sendUser.getUserId());
            jo2.put("user_name_from",sendUser.getName());
            jo2.put("user_head_from","http://resource.lichengwang.com/"+sendUser.getHead());
            jo2.put("user_id_to",receiveUsers.getUserId());
            jo2.put("user_name_to",receiveUsers.getName());
            jo2.put("user_head_to","http://resource.lichengwang.com/"+receiveUsers.getHead());
            jo.put("ext",jo2 );

            String parms = jo.toString();

            StringEntity entity = new StringEntity(parms,"utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            post.setHeader("Authorization", "Bearer "+token);
            try {
                HttpResponse result = httpClient.execute(post);
                statusCode = result.getStatusLine().getStatusCode();
                if(statusCode==HttpStatus.SC_OK){
                    // 请求结束，返回结果
					log.info(result.toString());
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
