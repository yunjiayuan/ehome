package com.busi.controller.api;

import com.busi.entity.CashOutOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /***
     * 查询提现记录列表
     * @param findType -1查询全部 2未到账 1已到账
     * @param userId   被查询的用户ID 0时为查询所有用户
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findCashOutList/{findType}/{userId}/{page}/{count}")
    ReturnData findRedPacketsList(@PathVariable int findType,@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 获取支付宝登录签名
     * @return
     */
    @GetMapping("getAliLoginSign")
    ReturnData getAliLoginSign();

    /***
     * 修改钱包提现功能的使用状态
     * @param type 0启用 1禁用
     * @return
     */
    @GetMapping("changePurseCaseOutStatus/{type}")
    ReturnData changePurseCaseOutStatus(@PathVariable int type);
}
