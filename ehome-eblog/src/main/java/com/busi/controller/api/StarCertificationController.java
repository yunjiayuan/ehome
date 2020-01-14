package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.StarCertification;
import com.busi.entity.UserAccountSecurity;
import com.busi.service.StarCertificationService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
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
    RedisUtils redisUtils;

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
        //验证是否实名
        int sign = userAccountSecurityUtils.testingReal(starCertification.getName() + "," + starCertification.getIdCard() + "," + starCertification.getUserId());
        if (sign == 0) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "您的身份证信息与您本人不符，请填写真实信息。", new JSONObject());
        }
        starCertification.setAddTime(new Date());
        starCertification.setState(1);//临时措施 暂无审核 先直接通过 suntj 20200106
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
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_STAR_CERTIFICATION + starCertification.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询认证详情
     * @param userId  被查询用户ID
     * @return
     */
    @Override
    public ReturnData findCertification(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_STAR_CERTIFICATION + userId);
        if (map == null || map.size() <= 0) {
            StarCertification certification = starCertificationService.find(userId);
            if (certification != null) {
                //放入缓存
                map = CommonUtils.objectToMap(certification);
                redisUtils.hmset(Constants.REDIS_KEY_STAR_CERTIFICATION + userId, map, -1);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
