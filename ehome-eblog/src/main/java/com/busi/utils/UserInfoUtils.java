package com.busi.utils;

import com.busi.entity.UserInfo;
import com.busi.fegin.UserInfoLocalControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 查询用户信息工具类
 * author：SunTianJie
 * create time：2018/9/7 19:08
 */
@Component
public class UserInfoUtils {

    @Autowired
    private UserInfoLocalControllerFegin userInfoLocalControllerFegin;

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
     * 更新用户生活圈首次发布视频标识
     * @param userInfo
     * @return
     */
    public void updateHomeBlogStatus(@RequestBody UserInfo userInfo) {
        userInfoLocalControllerFegin.updateHomeBlogStatus(userInfo);
    }
}
