package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.entity.UserMembership;
import com.busi.service.UserMembershipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户会员新增和更新接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController //此处必须继承BaseController和实现项目对应的接口TestApiController
public class UserMemberLController extends BaseController implements UserMemberLocalController {

    @Autowired
    private UserMembershipService userMembershipService;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 新增会员信息
     * @param userMembership
     * @return
     */
    @Override
    public ReturnData addUserMember(@RequestBody UserMembership userMembership) {
        int count =userMembershipService.add(userMembership);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"新增会员信息失败",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @Override
    public ReturnData updateUserMember(@RequestBody UserMembership userMembership) {
        int count =userMembershipService.update(userMembership);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新会员信息失败",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 查询用户会员信息
     * @param userId
     * @return
     */
    @Override
    public UserMembership getUserMember(@PathVariable(value="userId") long userId) {
        Map<String,Object> userMembershipMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+userId );
        UserMembership userMembership = null;
        if (userMembershipMap == null || userMembershipMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserMembership ump = userMembershipService.findUserMembership(userId);
            if (ump == null) {//数据库也没有
                return null;
            }
            userMembershipMap = CommonUtils.objectToMap(ump);
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP + userId, userMembershipMap, Constants.USER_TIME_OUT);
        }
        userMembership = (UserMembership) CommonUtils.mapToObject(userMembershipMap, UserInfo.class);
        return userMembership;
    }
}
