package com.busi.controller.api;

import com.busi.entity.CashOutOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 *  转账相关接口
 * author：SunTianJie
 * create time：2018/9/7 10:04
 */
public interface CashOutOrderApiController {

    /***
     * 提现接口
     * @param cashOut
     * @return
     */
    @PostMapping("cashOut")
    ReturnData cashOut(@Valid @RequestBody CashOutOrder cashOut, BindingResult bindingResult);
}
