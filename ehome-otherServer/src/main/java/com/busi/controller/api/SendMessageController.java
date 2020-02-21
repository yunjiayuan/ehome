package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 此处编写本类说明
 * authorsuntj
 * Create time 2020/2/21 21:14
 */
@RestController
public class SendMessageController extends BaseController implements SendMessageApiController {

    @Autowired
    MqUtils mqUtils;

    /**
     * 短信邀请
     * @param phone      将要发送短信的手机
     * @param phoneType  类型 默认0 可扩展
     * @param param      自定义参数
     * @return
     */
    @Override
    public ReturnData sendMessageByEpidemicSituation(@PathVariable String phone, @PathVariable int phoneType,@PathVariable String param) {
        //调用MQ进行发送短信
        mqUtils.sendPhoneMessage(phone,param,8);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
