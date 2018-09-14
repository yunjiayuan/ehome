package com.busi.utils;

import com.busi.entity.UserMembership;
import com.busi.fegin.UserMemberControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 查询用户会员信息工具类
 * author：SunTianJie
 * create time：2018/9/7 19:08
 */
@Component
public class UserMembershipUtils {

    @Autowired
    private UserMemberControllerFegin userMemberControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 根据用户ID 查询用户会员信息
     * @param userId
     * @return
     */
    public UserMembership getUserMemberInfo(long userId) {
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP + userId);
        UserMembership userMembership = null;
        if (userMap == null || userMap.size() <= 0) {
            userMembership = userMemberControllerFegin.getUserMember(userId);
        } else {
            userMembership = (UserMembership) CommonUtils.mapToObject(userMap, UserMembership.class);
        }
        return userMembership;
    }
}
