package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.ScenicSpotOrder;
import com.busi.service.TravelOrderService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 家门口旅游订单
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 13:31:21
 */
@RestController
public class TravelOrderLController extends BaseController implements TravelOrderLocalController {

    @Autowired
    TravelOrderService travelOrderService;

    /**
     * @param scenicSpotOrder
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @Override
    public ReturnData updatePayState(@RequestBody ScenicSpotOrder scenicSpotOrder) {
        scenicSpotOrder.setUpdateCategory(3);
        travelOrderService.updateOrders(scenicSpotOrder);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
