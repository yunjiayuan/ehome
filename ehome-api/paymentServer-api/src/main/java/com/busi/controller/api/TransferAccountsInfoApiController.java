package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.TransferAccountsInfo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  转账相关接口
 * author：SunTianJie
 * create time：2018/9/7 10:04
 */
public interface TransferAccountsInfoApiController {

    /***
     * 发送转账接口
     * @param transferAccountsInfo
     * @return
     */
    @PostMapping("addTransferAccountsInfo")
    ReturnData addTransferAccountsInfo(@Valid @RequestBody TransferAccountsInfo transferAccountsInfo, BindingResult bindingResult);

    /***
     * 根据转账ID查询转账信息
     * @param id
     * @return
     */
    @GetMapping("findTransferAccountsInfo/{id}")
    ReturnData findTransferAccountsInfo(@PathVariable String id);

    /***
     * 转账退款
     * @param id
     * @return
     */
    @GetMapping("transferAccountsRefund/{id}")
    ReturnData transferAccountsRefund(@PathVariable String id);


}
