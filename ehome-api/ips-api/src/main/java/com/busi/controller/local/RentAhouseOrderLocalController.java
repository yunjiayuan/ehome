package com.busi.controller.local;

import com.busi.entity.RentAhouseOrder;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 * RentAhouseOrder
 * author：zhaojiajie
 * create time：2021-03-30 15:15:06
 */
public interface RentAhouseOrderLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: usedDealOrders
     * @return:
     */
    @PutMapping("updatePayHouseType")
    ReturnData updatePayType(@RequestBody RentAhouseOrder usedDealOrders);
}
