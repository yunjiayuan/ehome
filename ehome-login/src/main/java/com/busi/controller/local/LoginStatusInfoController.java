package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.LoginStatusInfo;
import com.busi.entity.ReturnData;
import com.busi.service.LoginStatusinfoService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 同步登录信息接口
 * author：SunTianJie
 * create time：2018/7/13 16:09
 */
@RestController
public class LoginStatusInfoController extends BaseController implements LoginStatusInfoLocalController {

    @Autowired
    LoginStatusinfoService loginStatusinfoService;

    /***
     * 新增用户登录信息记录接口
     * @param loginStatusInfo
     * @return
     */
    @Override
    public ReturnData addLoginStatusInfo(@RequestBody LoginStatusInfo loginStatusInfo) {
        int count = loginStatusinfoService.add(loginStatusInfo);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"新增用户登录信息失败",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
