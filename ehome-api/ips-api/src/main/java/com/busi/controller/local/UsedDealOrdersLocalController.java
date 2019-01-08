package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.UsedDealOrders;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 * UsedDealOrders
 * author：zhaojiajie
 * create time：2019-1-8 17:13:17
 */
public interface UsedDealOrdersLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: usedDealOrders
     * @return:
     */
    @PutMapping("updatePayType")
    ReturnData updatePayType(@RequestBody UsedDealOrders usedDealOrders);
}
