package com.busi.controller.local;

import com.busi.entity.UserInfo;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class UserInfoLController implements UserInfoLocalController{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    /***
     * 查询用户信息
     * @param userId
     * @return
     */
    @Override
    public UserInfo getUserInfo(@PathVariable(value="userId") long userId) {
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
