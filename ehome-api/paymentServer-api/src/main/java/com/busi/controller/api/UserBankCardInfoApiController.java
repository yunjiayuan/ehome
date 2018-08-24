package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserBankCardInfo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 支付绑定银行卡相关接口
 * author：SunTianJie
 * create time：2018/8/24 16:06
 */
public interface UserBankCardInfoApiController {

    /***
     * 新增银行卡绑定信息
     * @param userBankCardInfo
     * @return
     */
    @PutMapping("addUserBankCardInfo")
    ReturnData addUserBankCardInfo(@Valid @RequestBody UserBankCardInfo userBankCardInfo, BindingResult bindingResult);
}
