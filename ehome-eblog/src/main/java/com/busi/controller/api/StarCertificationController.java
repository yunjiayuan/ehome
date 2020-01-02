package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.StarCertification;
import com.busi.entity.UserAccountSecurity;
import com.busi.service.StarCertificationService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserAccountSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: ehome
 * @description: 明星认证
 * @author: ZHaoJiaJie
 * @create: 2020-01-02 13:40
 */
@RestController
public class StarCertificationController extends BaseController implements StarCertificationApiController {

    @Autowired
    StarCertificationService starCertificationService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增认证
     * @param starCertification
     * @return
     */
    @Override
    public ReturnData addCertification(@Valid @RequestBody StarCertification starCertification, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        StarCertification certification = starCertificationService.find(starCertification.getUserId());
        if (certification != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(starCertification.getUserId());
        if (userAccountSecurity != null) {
            if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "您的身份证信息与您本人不符，请填写真实信息。", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "您的身份证信息与您本人不符，请填写真实信息。", new JSONObject());
        }
        starCertification.setAddTime(new Date());
        starCertificationService.add(starCertification);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新认证状态
     * @Param: starCertification
     * @return:
     */
    @Override
    public ReturnData updateCertification(@Valid @RequestBody StarCertification starCertification, BindingResult bindingResult) {
        starCertificationService.update(starCertification);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询认证状态
     * @param userId  被查询用户ID
     * @return
     */
    @Override
    public ReturnData findCertification(@PathVariable long userId) {
        StarCertification certification = starCertificationService.find(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("state", certification.getState());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
