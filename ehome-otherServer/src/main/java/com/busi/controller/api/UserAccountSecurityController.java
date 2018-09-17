package com.busi.controller.api;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户账户安全接口
 * author：SunTianJie
 * create time：2018/9/17 15:07
 */
@RestController
public class UserAccountSecurityController extends BaseController implements UserAccountSecurityApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 新增安全中心数据接口
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData addUserAccountSecurity(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        return null;
    }

    /***
     * 查询安全中心数据接口
     * @param userId
     * @return
     */
    @Override
    public ReturnData findUserAccountSecurity(@PathVariable long userId) {
        if(userId<=0){//参数有误
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY+userId);
        if(map==null||map.size()<=0){
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurity(userId);
            if(userAccountSecurity==null){
                //之前该用户未设置过安全中心数据
                userAccountSecurity = new UserAccountSecurity();
                userAccountSecurity.setUserId(userId);
            }else{
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY+userId,CommonUtils.objectToMap(userAccountSecurity),Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userAccountSecurity);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 修改安全中心数据接口
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData updateUserAccountSecurity(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        return null;
    }
}
