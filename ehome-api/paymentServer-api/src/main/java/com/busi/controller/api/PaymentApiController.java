package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 支付相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
public interface PaymentApiController {

    /***
     * 获取私钥
     * @return
     */
    @GetMapping("getPaymentKey")
    ReturnData getPaymentKey();
}
