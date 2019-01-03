package com.busi.controller.api;

import org.springframework.stereotype.Controller;
import java.util.HashMap;

/**
 * 云家园隐私政策说明 静态网页
 * author：SunTianJie
 * create time：2019/1/2 15:41
 */
@Controller
public class PrivacyPolicyHtmlController implements PrivacyPolicyHtmlApiController{

    public String privacyPolicy(HashMap<String, Object> map) {
        return "privacyPolicy/privacyPolicy.html";
    }
}
