package com.busi.utils;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.controller.local.UserInfoLocalController;
import com.busi.entity.ReturnData;
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
public class UserInfoUtils extends BaseController {

    @Autowired
    private UserInfoLocalControllerFegin userInfoLocalControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 根据用户ID 查询用户信息
     * @param userId
     * @return
     */
    public UserInfo getUserInfo(long userId) {
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
        UserInfo userInfo = null;
        if (userMap == null || userMap.size() <= 0) {
            userInfo = userInfoLocalControllerFegin.getUserInfo(userId);
        } else {
            userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
        }
        return userInfo;
    }

    /***
     * 更新用户新人红包标识
     * @param userInfo
     * @return
     */
    public ReturnData updateIsNew(@RequestBody UserInfo userInfo) {
        userInfoLocalControllerFegin.updateIsNew(userInfo);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新用户代言人标识
     * @param type    代言人类型  0不是 1是地区代言人
     * @param name    代言人名称 例如：北京海淀代言人
     * @param userId  当前用户ID
     */
    public void updateSpokesmanStatus(long userId, int type, String name) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setIsSpokesman(type);
        userInfo.setSpokesmanName(name);
        userInfoLocalControllerFegin.updateSpokesmanStatus(userInfo);
    }
}
