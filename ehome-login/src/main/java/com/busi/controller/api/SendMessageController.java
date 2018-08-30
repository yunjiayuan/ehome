package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送信息接口 例如发送短信验证码  发送邮件 发送消息等
 * author：SunTianJie
 * create time：2018/8/30 8:46
 */
@RestController
public class SendMessageController extends BaseController implements SendMessageApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    /**
     * 发送手机短信
     * @param phone     将要发送短信的手机号
     * @param phoneType 短信类型 0注册验证码  1找回支付密码验证码 2安全中心绑定手机验证码 3安全中心解绑手机验证码
     *                            4手机短信找回登录密码验证码  5手机短信修改密码验证码 6短信邀请新用户注册 7...
     * @return
     */
    @Override
    public ReturnData SendPhoneMessage(@PathVariable String phone,@PathVariable int phoneType) {
        //验证参数
        if(CommonUtils.checkFull(phone)||!CommonUtils.checkPhone(phone)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"phone参数有误",new JSONObject());
        }
        if(phoneType<1||phoneType>6){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"phoneType参数有误，超出合法范围",new JSONObject());
        }
        //生成验证码
        String code = CommonUtils.getRandom(4,1);
        //根据业务不同将验证码存入缓存中
        switch (phoneType) {

            case 0://注册验证码（暂不使用，使用注册接口中的获取验证码）

                break;
            case 1://找回支付密码验证码
                redisUtils.set(Constants.REDIS_KEY_PAY_FIND_PAYPASSWORD_CODE+CommonUtils.getMyId(),code,60*10);//验证码10分钟内有效
                break;
            case 2://安全中心绑定手机验证码

                break;
            case 3://安全中心解绑手机验证码

                break;
            case 4://手机短信找回登录密码验证码

                break;
            case 5://手机短信修改密码验证码

                break;
            case 6://短信邀请新用户注册

                break;

            default:
                break;
        }
        //调用MQ进行发送短信
        mqUtils.sendPhoneMessage(phone,code,phoneType);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
