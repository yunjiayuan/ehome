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

    /***
     * 更新用户V认证标识
     * @param type    0表示更新为 未认证 1表示更新为 已认证
     * @param userId  当前用户ID
     */
    public void updateUserCe(long userId ,int type) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUser_ce(type);
        userInfoLocalControllerFegin.updateUserCe(userInfo);
    }

    /***
     * 更新用户找人倾诉状态
     * @param type    倾诉状态 0表示不接受倾诉  1表示接受倾诉
     * @param userId  当前用户ID
     */
    public void updateTalkToSomeoneStatus(long userId ,int type) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setTalkToSomeoneStatus(type);
        userInfoLocalControllerFegin.updateTalkToSomeoneStatus(userInfo);
    }

    /***
     * 更新用户聊天互动状态
     * @param type    聊天互动功能的状态 0表示不接受别人找你互动  1表示接受别人找你互动
     * @param userId  当前用户ID
     */
    public void updateChatnteractionStatus(long userId ,int type) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setChatnteractionStatus(type);
        userInfoLocalControllerFegin.updateChatnteractionStatus(userInfo);
    }

    /***
     * 更新用户代言人标识
     * @param type    代言人类型  0不是 1是地区代言人
     * @param name    代言人名称 例如：北京海淀代言人
     * @param userId  当前用户ID
     */
    public void updateSpokesmanStatus(long userId ,int type ,String name) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setIsSpokesman(type);
        userInfo.setSpokesmanName(name);
        userInfoLocalControllerFegin.updateSpokesmanStatus(userInfo);
    }
}
