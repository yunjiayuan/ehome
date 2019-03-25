package com.busi.controller.local;


import com.busi.entity.KitchenOrders;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 * KitchenOrdersLocalController
 * author：zhaojiajie
 * create time：2019-3-25 13:25:03
 */
public interface KitchenOrdersLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: kitchenOrders
     * @return:
     */
    @PutMapping("updatePayState")
    ReturnData updatePayState(@RequestBody KitchenOrders kitchenOrders);
}
