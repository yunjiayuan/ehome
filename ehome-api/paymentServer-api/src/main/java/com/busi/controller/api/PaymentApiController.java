package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    /***
     * 查询支付密码设置信息和银行卡绑定信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @GetMapping("findPayPasswordInfo/{userId}")
    ReturnData findPayPasswordInfo(@PathVariable long userId);
}
