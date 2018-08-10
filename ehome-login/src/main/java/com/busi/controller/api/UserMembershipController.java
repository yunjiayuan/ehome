package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserMembership;
import com.busi.service.UserMembershipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 会员相关接口
 * author：SunTianJie
 * create time：2018/8/9 14:34
 */
@RestController
public class UserMembershipController extends BaseController implements UserMembershipApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserMembershipService userMembershipService;

    /***
     * 查询用户会员信息
     * @param userId 被查询者的用户ID
     * @return
     */
    @Override
    public ReturnData findUserMembershipInfo(@PathVariable long userId) {
        //验证修改人权限
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userId+"]的会员信息",new JSONObject());
        }
        Map<String,Object> userMembershipMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+userId );
        if(userMembershipMap==null||userMembershipMap.size()<=0){
            UserMembership userMembership = null;
            //缓存中没有用户对象信息 查询数据库
            userMembership = userMembershipService.findUserMembership(userId);
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(userId);
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            userMembershipMap = CommonUtils.objectToMap(userMembership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP+userId,userMembershipMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userMembershipMap);
    }
}
