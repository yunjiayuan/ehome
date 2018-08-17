package com.busi.controller.local;

import com.busi.entity.Purse;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 钱包相关接口(通过fegin本地内部调用)
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
public interface PurseLocalController {

    /***
     * 更新用户账户信息接口(包含新增逻辑)
     * @param purse
     * @return
     */
    @PutMapping("updatePurseInfo")
    ReturnData updatePurseInfo(@Valid @RequestBody Purse purse, BindingResult bindingResult);

}
