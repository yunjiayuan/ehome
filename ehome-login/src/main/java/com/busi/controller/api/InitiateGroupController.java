package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/8/1 11:07
 */
@RestController
public class InitiateGroupController extends BaseController implements InitiateGroupApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    /***
     * 查询指定群成员的用户信息
     * @param userIds 将要查询的用户ID组合 格式123,456
     * @return
     */
    @Override
    public ReturnData findInitiateGroupMemberInfo(@PathVariable String userIds) {
        //验证参数
        if(CommonUtils.checkFull(userIds)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userIds参数有误",new JSONObject());
        }
        List list = new ArrayList();
        String[] key = userIds.split(",");
        for(int k=0;k<key.length;k++){
            String userId = key[k];
            if(CommonUtils.checkFull(userId))continue;
            Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId );
            if(userMap==null||userMap.size()<=0){
                //缓存中没有用户对象信息 查询数据库
                UserInfo userInfo = userInfoService.findUserById(Long.parseLong(userId));
                if(userInfo==null)continue;//数据库也没有
                userMap = CommonUtils.objectToMap(userInfo);
                //将用户信息存入缓存中 无论缓存中是否已有 直接覆盖
                redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
            }
            //缓存中存在用户实体 放入集合中
            userMap.put("password","");
            userMap.put("im_password","");
            list.add(userMap);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",list);
    }
}
