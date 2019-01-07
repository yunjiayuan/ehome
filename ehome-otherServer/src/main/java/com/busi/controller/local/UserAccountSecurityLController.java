package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserAccountSecurity;
import com.busi.service.UserAccountSecurityService;
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
 * 用户账户安全信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/9/18 18:31
 */
@RestController
public class UserAccountSecurityLController extends BaseController implements  UserAccountSecurityLocalController{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 查询用户安全信息
     * @param userId
     * @return
     */
    @Override
    public UserAccountSecurity getUserAccountSecurity(@PathVariable(value="userId") long userId) {
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
        UserAccountSecurity userAccountSecurity = null;
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
            if (uas == null) {//数据库也没有
                return null;
            }
            uas.setRedisStatus(1);
            userAccountSecurityMap = CommonUtils.objectToMap(uas);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, userAccountSecurityMap, Constants.USER_TIME_OUT);
        }
        userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
        return userAccountSecurity;
    }

    /***
     * 更新安全中心信息（目前只提供手机和第三方注册新用户时同步安全中心信息和完善资料时绑定门牌号时使用）
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData addAccountSecurity(@RequestBody UserAccountSecurity userAccountSecurity) {
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        UserAccountSecurity u = null;
        int count = 0;
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            u = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
        }else{
            if(Integer.parseInt(userAccountSecurityMap.get("redisStatus").toString())==1) {//redisStatus==1 说明数据中已有记录
                u =(UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap,UserAccountSecurity.class);
            }
        }
        //处理特殊字符  20190107  ZHJJ
        String name = userAccountSecurity.getOtherPlatformAccount();
        if (!CommonUtils.checkFull(name)) {
            String filteringTitle = CommonUtils.filteringContent(name);
            if (!CommonUtils.checkFull(filteringTitle)) {
                userAccountSecurity.setOtherPlatformAccount(filteringTitle);
            }
        }
        if(u==null){//新增
            count =userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
        }else{//更新
            u.setOtherPlatformType(userAccountSecurity.getOtherPlatformType());
            u.setOtherPlatformAccount(userAccountSecurity.getOtherPlatformAccount());
            u.setOtherPlatformKey(userAccountSecurity.getOtherPlatformKey());
            u.setPhone(userAccountSecurity.getPhone());
            count = userAccountSecurityService.updateUserAccountSecurity(u);
        }
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新安全中心信息失败",new JSONObject());
        }
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY+userAccountSecurity.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

}
