package com.busi.controller.local;

import com.busi.entity.LoginStatusInfo;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录信息同步接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface LoginStatusInfoLocalController {

    /***
     * 新增用户登录信息记录接口
     * @param loginStatusInfo
     * @return
     */
    @PostMapping("addLoginStatusInfo")
    ReturnData addLoginStatusInfo(@RequestBody LoginStatusInfo loginStatusInfo);

}
