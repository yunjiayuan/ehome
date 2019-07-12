package com.busi.controller.local;

import com.busi.entity.KitchenBookedOrders;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 厨房订座订单
 * @author: ZHaoJiaJie
 * @create: 2019-07-12 15:25
 */
public interface KitchenBookedOrdersLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: kitchenBookedOrders
     * @return:
     */
    @PutMapping("updatePayState")
    ReturnData updatePayState(@RequestBody KitchenBookedOrders kitchenBookedOrders);
}
