package com.busi.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

/**
 * 云家园隐私政策说明 静态网页
 * author：SunTianJie
 * create time：2019/1/2 15:55
 */
public interface PrivacyPolicyHtmlApiController {

    /***
     * 隐私协议在线页面
     * @param map
     * @return
     */
    @GetMapping("/privacyPolicy")
    String privacyPolicy(HashMap<String, Object> map) ;

    /***
     * 下载界面（停用）
     * @param map
     * @param shareCode
     * @return
     */
    @GetMapping("/downLoad/{shareCode}")
    String downLoad(HashMap<String, Object> map,@PathVariable String shareCode) ;
}
