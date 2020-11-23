package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.DoorwayBusinessOrder;
import com.busi.entity.ReturnData;
import com.busi.service.DoorwayBusinessOrderService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 家门口商家订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 13:48:59
 */
@RestController
public class DoorwayBusinessOrderLController extends BaseController implements DoorwayBusinessOrderLocalController {

    @Autowired
    DoorwayBusinessOrderService hotelOrderService;

    /**
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @Override
    public ReturnData updatePayState(@RequestBody DoorwayBusinessOrder hotelOrder) {
        hotelOrder.setUpdateCategory(4);
        hotelOrderService.updateOrders(hotelOrder);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
