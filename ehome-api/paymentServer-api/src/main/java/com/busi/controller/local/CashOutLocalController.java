package com.busi.controller.local;

import com.busi.entity.CashOutOrder;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 提现相关接口(通过fegin本地内部调用)
 * author：SunTianJie
 * create time：2020-7-9 23:07:26
 */
public interface CashOutLocalController {

    /***
     *  提现同步到微信或者支付宝
     * @param cashOutOrder
     * @return
     */
    @PutMapping("cashOutToOther")
    ReturnData cashOutToOther(@RequestBody CashOutOrder cashOutOrder);

}
