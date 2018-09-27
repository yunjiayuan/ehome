package com.busi.controller.local;

import com.busi.controller.BaseController;
import com.busi.entity.UserAccountSecurity;
import com.busi.service.UserAccountSecurityService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
}
