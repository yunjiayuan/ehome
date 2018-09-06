package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserMembership;
import com.busi.service.UserMembershipService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户会员新增和更新接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController //此处必须继承BaseController和实现项目对应的接口TestApiController
public class UserMemberLController extends BaseController implements UserMemberLocalController {

    @Autowired
    private UserMembershipService userMembershipService;
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
}
