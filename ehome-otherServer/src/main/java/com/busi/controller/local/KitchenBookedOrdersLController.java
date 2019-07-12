package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.KitchenBookedOrders;
import com.busi.entity.ReturnData;
import com.busi.service.KitchenBookedOrdersService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 厨房订座订单
 * @author: ZHaoJiaJie
 * @create: 2019-07-12 15:27
 */
@RestController
public class KitchenBookedOrdersLController extends BaseController implements KitchenBookedOrdersLocalController {


    @Autowired
    KitchenBookedOrdersService kitchenBookedOrdersService;

    /**
     * @program: ehome
     * @description: 厨房订座订单
     * @author: ZHaoJiaJie
     * @create: 2019-7-12 15:30:47
     */
    @Override
    public ReturnData updatePayState(@RequestBody KitchenBookedOrders kitchenBookedOrders) {

        kitchenBookedOrders.setUpdateCategory(4);
        kitchenBookedOrdersService.updateOrders(kitchenBookedOrders);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
