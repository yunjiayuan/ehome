package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserBankCardInfo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("addUserBankCardInfo")
    ReturnData addUserBankCardInfo(@Valid @RequestBody UserBankCardInfo userBankCardInfo, BindingResult bindingResult);

    /***
     *
     * @param userId     用户ID
     * @param bankCard   银行卡号
     * @param bankPhone  银行预留手机号
     * @param bankName   银行姓名
     * @param bankCardNo 银行预留省份证号
     * @return 返回 私钥 用于重置支付密码的凭据

    @GetMapping("checkUserBankCardInfo/{userId}/{bankCard}/{bankPhone}/{bankName}/{bankCardNo}")
    ReturnData checkUserBankCardInfo(@PathVariable long userId,@PathVariable String bankCard,@PathVariable String bankPhone,
                                @PathVariable String bankName,@PathVariable String bankCardNo);*/
    /**
     * 验证提交的银行卡信息与该用户绑定银行卡信息是否一致
     * @param userBankCardInfo
     * @param bindingResult
     * @return 返回 私钥 用于重置支付密码的凭据
     */
    @PutMapping("checkUserBankCardInfo")
    ReturnData checkUserBankCardInfo(@Valid @RequestBody UserBankCardInfo userBankCardInfo, BindingResult bindingResult);
}
