package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HotelTourismBookedOrders;
import com.busi.entity.ReturnData;
import com.busi.service.HotelTourismBookedOrdersService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 家门口酒店、景区订座订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-24 13:23:23
 */
@RestController
public class HotelTourismBookedOrdersLController extends BaseController implements HotelTourismBookedOrdersLocalController {

    @Autowired
    HotelTourismBookedOrdersService hotelOrderService;

    /**
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @Override
    public ReturnData updatePayState(@RequestBody HotelTourismBookedOrders hotelOrder) {
        hotelOrder.setUpdateCategory(4);
        hotelOrderService.updateOrders(hotelOrder);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
