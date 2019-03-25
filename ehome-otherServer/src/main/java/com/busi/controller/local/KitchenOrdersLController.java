package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.KitchenOrders;
import com.busi.entity.ReturnData;
import com.busi.service.KitchenOrdersService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 厨房订单
 * @author: ZHaoJiaJie
 * @create: 2019-03-25 13:26
 */
@RestController
public class KitchenOrdersLController extends BaseController implements KitchenOrdersLocalController {

    @Autowired
    KitchenOrdersService kitchenOrdersService;

    /**
     * @program: ehome
     * @description: 厨房订单
     * @author: ZHaoJiaJie
     * @create: 2019-3-25 13:35:52
     */
    @Override
    public ReturnData updatePayState(@RequestBody KitchenOrders kitchenOrders) {

        kitchenOrders.setUpdateCategory(6);
        kitchenOrdersService.updateOrders(kitchenOrders);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
