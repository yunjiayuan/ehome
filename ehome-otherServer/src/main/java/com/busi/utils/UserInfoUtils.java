package com.busi.utils;

import com.alibaba.fastjson.JSONObject;
import com.busi.entity.UserInfo;
import com.busi.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 用户信息相关工具
 *
 * @program: ehome
 * @description:
 * @author: ZHaoJiaJie
 * @create: 2018-09-07 10:32
 */
@Component
public class UserInfoUtils {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    public UserInfo getUserInfo(long userId) {
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
        UserInfo userInfo = null;
        if (userMap == null || userMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserInfo u = userInfoService.findUserById(userId);
            if (u == null) {//数据库也没有
                return null;
            }
            userMap = CommonUtils.objectToMap(u);
            redisUtils.hmset(Constants.REDIS_KEY_USER + userId, userMap, Constants.USER_TIME_OUT);
        }
        userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
        return userInfo;
    }
}
