package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorMasterOrders;
import com.busi.entity.ShopFloorOrders;
import com.busi.service.ShopFloorMasterOrdersService;
import com.busi.service.ShopFloorOrdersService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 更新楼店订单支付状态
 * @author: ZhaoJiaJie
 * @create: 2019-12-17 15:07
 */
@RestController
public class ShopFloorOrdersLController extends BaseController implements ShopFloorOrdersLocalController {
    @Autowired
    ShopFloorOrdersService shopFloorOrdersService;

    @Autowired
    ShopFloorMasterOrdersService shopFloorMasterOrdersService;


    /***
     * 更新用户订单支付状态
     * @return
     */
    @Override
    public ReturnData updatePayType(@RequestBody ShopFloorOrders shopFloorOrders) {

        shopFloorOrders.setUpdateCategory(4);
        shopFloorOrdersService.updateOrders(shopFloorOrders);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新店主订单支付状态
     * @return
     */
    @Override
    public ReturnData updateMasterPay(@RequestBody ShopFloorMasterOrders shopFloorOrders) {

        shopFloorOrders.setUpdateCategory(4);
        shopFloorMasterOrdersService.updateOrders(shopFloorOrders);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
