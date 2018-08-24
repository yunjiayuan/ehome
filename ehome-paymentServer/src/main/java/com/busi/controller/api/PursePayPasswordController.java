package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.PursePayPassword;
import com.busi.entity.ReturnData;
import com.busi.service.PursePayPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 支付密码相关接口
 * author：SunTianJie
 * create time：2018/8/24 16:06
 */
@RestController
public class PursePayPasswordController extends BaseController implements PursePayPasswordApiController {

    @Autowired
    private PursePayPasswordService pursePayPasswordService;

    /***
     * 设置支付密码
     * @param pursePayPassword
     * @return
     */
    @Override
    public ReturnData addPursePayPassword(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult) {
        return null;
    }

    /***
     * 修改支付密码
     * @param pursePayPassword
     * @return
     */
    @Override
    public ReturnData updatePursePayPassword(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult) {
        return null;
    }
}
