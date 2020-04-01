package com.busi.utils;

import com.busi.entity.UserInfo;
import com.busi.fegin.UserInfoLocalControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 查询用户信息工具类
 * author：SunTianJie
 * create time：2018/9/7 19:08
 */
@Component
public class UserInfoUtils {

    @Autowired
    UserInfoLocalControllerFegin userInfoLocalControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 根据用户ID 查询用户信息
     * @param userId
     * @return
     */
    public UserInfo getUserInfo(long userId){
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
        UserInfo userInfo = null;
        if (userMap == null || userMap.size() <= 0) {
            userInfo = userInfoLocalControllerFegin.getUserInfo(userId);
        }else{
            userInfo = (UserInfo) CommonUtils.mapToObject(userMap,UserInfo.class);
        }
        return userInfo;
    }

    /***
     * 根据用户门牌号查询用户信息
     * @param houseNumber 0_1001518
     * @return
     */
    public UserInfo getUserInfo(String houseNumber){
        Object obj = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER, houseNumber);
        UserInfo userInfo = null;
        if (obj == null || CommonUtils.checkFull(String.valueOf(obj.toString()))) {
            userInfo = userInfoLocalControllerFegin.getUserInfoByHouseNumber(houseNumber);
        }else{
            long userId = Long.parseLong(obj.toString());
            Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
            if (userMap != null && userMap.size() > 0) {
                userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
            } else {
                userInfo = userInfoLocalControllerFegin.getUserInfoByHouseNumber(houseNumber);
            }
        }
        return userInfo;
    }
}
