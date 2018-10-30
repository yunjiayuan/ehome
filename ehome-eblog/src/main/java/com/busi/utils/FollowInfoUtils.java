package com.busi.utils;

import com.busi.entity.UserInfo;
import com.busi.fegin.FollowInfoLocalControllerFegin;
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
public class FollowInfoUtils {

    @Autowired
    private FollowInfoLocalControllerFegin followInfoLocalControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 根据用户ID 查询用户信息
     * @param userId
     * @return
     */
    public String getFollowInfo(long userId){
        Object follow = redisUtils.getKey(Constants.REDIS_KEY_FOLLOW_LIST+userId);
        if(follow!=null&&!CommonUtils.checkFull(follow.toString())){
            return follow.toString();
        }else{
            return followInfoLocalControllerFegin.getFollowInfo(userId);
        }
    }
}
