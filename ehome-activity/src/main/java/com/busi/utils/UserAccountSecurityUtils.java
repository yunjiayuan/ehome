package com.busi.utils;

import com.busi.entity.UserAccountSecurity;
import com.busi.fegin.UserAccountSecurityLocalControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 查询用户账户安全信息
 * author：SunTianJie
 * create time：2018/9/7 19:08
 */
@Component
public class UserAccountSecurityUtils {
    @Autowired
    private UserAccountSecurityLocalControllerFegin userAccountSecurityFegin;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 根据用户ID 查询用户信息
     * @param userId
     * @return
     */
    public UserAccountSecurity getUserAccountSecurity(long userId) {
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
        UserAccountSecurity userAccountSecurity = null;
        if (userMap == null || userMap.size() <= 0) {
            userAccountSecurity = userAccountSecurityFegin.getUserAccountSecurity(userId);
        } else {
            userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(userMap, UserAccountSecurity.class);
        }
        return userAccountSecurity;
    }
}
