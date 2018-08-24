package com.busi.controller.api;

import com.busi.entity.PursePayPassword;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 支付密码相关接口
 * author：SunTianJie
 * create time：2018/8/24 16:06
 */
public interface PursePayPasswordApiController {

    /***
     * 设置支付密码
     * @param pursePayPassword
     * @return
     */
    @PostMapping("addPursePayPassword")
    ReturnData addPursePayPassword(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult);

    /***
     * 修改支付密码
     * @param pursePayPassword
     * @return
     */
    @PutMapping("updatePursePayPassword")
    ReturnData updatePursePayPassword(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult);
}
