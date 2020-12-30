package com.busi.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

/**
 * 云家园隐私政策说明 静态网页
 * author：SunTianJie
 * create time：2019/1/2 15:41
 */
@Controller
public class PrivacyPolicyHtmlController implements PrivacyPolicyHtmlApiController{

    @Override
    public String privacyPolicy(HashMap<String, Object> map) {
        return "privacyPolicy/privacyPolicy.html";
    }

    @Override
    public String downLoad(HashMap<String, Object> map,@PathVariable String shareCode) {
        map.put("shareCode",shareCode);
        return "downLoad/index.html";
    }
}
