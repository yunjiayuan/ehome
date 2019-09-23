package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ChatAutomaticRecovery;
import com.busi.entity.ChatSquare;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.service.ChatSquareService;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * 聊天广场马甲功能接口
 * author：SunTianJie
 * create time：2018/10/10 16:31
 */
@RestController
public class ChatSquareController extends BaseController implements ChatSquareApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ChatSquareService chatSquareService;

    @Autowired
    UserInfoService userInfoService;

    /***
     * 查询用户马甲信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findChatSquare(@PathVariable long userId) {
        //验证参数
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userID参数有误", new JSONObject());
        }
        //开始查询
        ChatSquare chatSquare = chatSquareService.findChatSquareByUserId(userId);
        if (chatSquare == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", chatSquare);
    }

    /***
     * 新增或修改用户马甲接口
     * @param chatSquare
     * @return
     */
    @Override
    public ReturnData updateChatSquare(@Valid @RequestBody ChatSquare chatSquare, BindingResult bindingResult) {
        //验证参数
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区正确性
        if (!CommonUtils.checkProvince_city_district(chatSquare.getCountry(), chatSquare.getProvince(), chatSquare.getCity(), chatSquare.getDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "国家、省、市、区参数不匹配", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != chatSquare.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + chatSquare.getUserId() + "]的马甲信息", new JSONObject());
        }
        //判断之前是否设置过
        ChatSquare cs = chatSquareService.findChatSquareByUserId(chatSquare.getUserId());
        if (cs == null) {//新增
            chatSquareService.add(chatSquare);
        } else {//更新
            chatSquareService.update(chatSquare);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 清除用户马甲信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData delChatSquare(@PathVariable long userId) {
        //验证参数
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的马甲信息", new JSONObject());
        }
        //开始删除
        chatSquareService.delChatSquareByUserId(userId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询指定聊天室成员列表的用户信息
     * @param userIds 将要查询的用户ID组合 格式123,456
     * @return
     */
    @Override
    public ReturnData findChatSquareUserInfo(@PathVariable String userIds) {
        //验证参数
        if (CommonUtils.checkFull(userIds)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userIds参数不能为空", new JSONObject());
        }
        //查询是否设置过马甲
        List chatSquaresList = chatSquareService.findChatSquareUserInfo(userIds.split(","));
        //查询用户信息
        List list = new ArrayList();
        String[] key = userIds.split(",");
        for (int k = 0; k < key.length; k++) {
            String userId = key[k];
            if (CommonUtils.checkFull(userId)) continue;
            Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
            if (userMap == null || userMap.size() <= 0) {
                //缓存中没有用户对象信息 查询数据库
                UserInfo userInfo = userInfoService.findUserById(Long.parseLong(userId));
                if (userInfo == null) continue;//数据库也没有
                userMap = CommonUtils.objectToMap(userInfo);
                //将用户信息存入缓存中 无论缓存中是否已有 直接覆盖
                redisUtils.hmset(Constants.REDIS_KEY_USER + userInfo.getUserId(), userMap, Constants.USER_TIME_OUT);
            }
            //缓存中存在用户实体 放入集合中
            userMap.put("password", "");
            userMap.put("im_password", "");
            UserInfo userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
            if (chatSquaresList != null && chatSquaresList.size() > 0) {
                for (int i = 0; i < chatSquaresList.size(); i++) {
                    ChatSquare chatSquare = (ChatSquare) chatSquaresList.get(i);
                    if (chatSquare != null && chatSquare.getUserId() == Long.parseLong(userId)) {//已设置马甲
                        userInfo.setIsVest(1);
                        userInfo.setVestId(chatSquare.getId());
                        userInfo.setName(chatSquare.getName());
                        userInfo.setCountry(chatSquare.getCountry());
                        userInfo.setProvince(chatSquare.getProvince());
                        userInfo.setCity(chatSquare.getCity());
                        userInfo.setDistrict(chatSquare.getDistrict());
                        userInfo.setSex(chatSquare.getSex());
                        userInfo.setBirthday(chatSquare.getBirthday());
                        userInfo.setStudyRank(chatSquare.getStudyRank());
                        userInfo.setMaritalStatus(chatSquare.getMaritalStatus());
                        userInfo.setGxqm(chatSquare.getGxqm());
                    }
                }
            }
            list.add(userInfo);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 初始化聊天广场每个聊天室在线人员
     * @param proTypeId  省Id
     * @return
     */
    @Override
    public ReturnData initialChatroom(@PathVariable int proTypeId) {
        if (proTypeId < 0 || proTypeId > 33) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "proTypeId参数有误", new JSONObject());
        }
        //查询缓存
        String users = "";
        Map<String, Object> map = new HashMap<>();
        Object chatSquare = redisUtils.getKey(Constants.REDIS_KEY_CHAT_SQUARE + proTypeId);
        if (chatSquare == null) {
            //缓存没有，随机50-100个机器人
            int countTatol = 0;
            Random random = new Random();
            countTatol = random.nextInt(51) + 50;
            for (int i = 0; i < countTatol; i++) {
                long newUserId = random.nextInt(40001) + 13870;
                if (i == (countTatol - 1)) {
                    users += newUserId;
                } else {
                    users += newUserId + ",";
                }
            }
            //计算当前时间 到 今天晚上12点的秒数差
            long second = CommonUtils.getCurrentTimeTo_12();
            //存入缓存
            if (!CommonUtils.checkFull(users)) {
                redisUtils.set(Constants.REDIS_KEY_CHAT_SQUARE + proTypeId, users, second);
            }
            map.put("users", users);
        } else {
            map.put("users", chatSquare);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 获取聊天自动回复内容
     * @param chatAutomaticRecovery
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData automaticRecoveryContent(@Valid @RequestBody ChatAutomaticRecovery chatAutomaticRecovery, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        if(CommonUtils.getMyId()!=chatAutomaticRecovery.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"当前登录用户无权限操作用户["+chatAutomaticRecovery.getUserId()+"]的聊天信息",new JSONObject());
        }
        //开始调用机器人自动回复
        int statusCode = 0;
        JSONObject jo = new JSONObject();
        JSONObject jo2 = new JSONObject();
        JSONObject joText = new JSONObject();
        JSONObject joImage = new JSONObject();
        JSONObject joSelfInfo = new JSONObject();
        JSONObject joLocation = new JSONObject();
        JSONObject jo4 = new JSONObject();
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(Constants.TULING_URL);
        //设置消息类型
        jo.put("reqType", chatAutomaticRecovery.getContentType());
        //设置文本内容
        joText.put("text",chatAutomaticRecovery.getContent());
        jo2.put("inputText",joText);
        //设置图片和音频内容
        joImage.put("url",chatAutomaticRecovery.getContent());
        jo2.put("inputImage",joImage);
        //设置位置信息
        joLocation.put("city","");
        joLocation.put("province","");
        joLocation.put("street","");
        joSelfInfo.put("location",joLocation);
        jo2.put("selfInfo",joSelfInfo);

        jo.put("perception", jo2);
        //设置用户信息和key
        jo4.put("apiKey",Constants.TULING_KEY);
        jo4.put("userId",chatAutomaticRecovery.getUserId());
        jo.put("userInfo", jo4);

        String parms = jo.toString();

        StringEntity entity = new StringEntity(parms,"utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        String recoveryContent = "你好，我现在有事，稍后再联系你哈";//自动回复内容
        try {
            HttpResponse result = httpClient.execute(post);
            statusCode = result.getStatusLine().getStatusCode();
            if(statusCode==HttpStatus.SC_OK){
                // 请求结束，返回结果
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(result.getEntity()));
                JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("results"));
                String values = jsonArray.getJSONObject(0).getString("values");
                recoveryContent = JSONObject.parseObject(values).getString("text");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "获取用户["+chatAutomaticRecovery.getUserId()+"]的聊天信息异常，Json解析错误",new JSONObject());
        } catch (IOException e) {
            e.printStackTrace();
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "获取用户["+chatAutomaticRecovery.getUserId()+"]的聊天信息异常，Json解析错误2",new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("recoveryContent",recoveryContent);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success",map);
    }
}
