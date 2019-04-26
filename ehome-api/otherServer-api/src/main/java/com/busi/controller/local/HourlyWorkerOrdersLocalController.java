package com.busi.controller.local;

import com.busi.entity.HourlyWorkerOrders;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 小时工订单
 * @author: ZHaoJiaJie
 * @create: 2019-04-26 11:40
 */
public interface HourlyWorkerOrdersLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: hourlyWorkerOrders
     * @return:
     */
    @PutMapping("updatePayStates")
    ReturnData updatePayStates(@RequestBody HourlyWorkerOrders hourlyWorkerOrders);
}
