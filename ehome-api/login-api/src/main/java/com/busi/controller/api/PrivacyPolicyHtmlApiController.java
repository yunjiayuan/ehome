package com.busi.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;

/**
 * 云家园隐私政策说明 静态网页
 * author：SunTianJie
 * create time：2019/1/2 15:55
 */
public interface PrivacyPolicyHtmlApiController {

    @GetMapping("/privacyPolicy")
    String privacyPolicy(HashMap<String, Object> map) ;

    @GetMapping("/downLoad")
    String downLoad(HashMap<String, Object> map) ;
}
