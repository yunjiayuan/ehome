package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HotelOrder;
import com.busi.entity.ReturnData;
import com.busi.service.HotelOrderService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 家门口酒店民宿订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 17:06:12
 */
@RestController
public class HotelOrderLController extends BaseController implements HotelOrderLocalController {

    @Autowired
    HotelOrderService hotelOrderService;

    /**
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @Override
    public ReturnData updatePayState(@RequestBody HotelOrder hotelOrder) {
        hotelOrder.setUpdateCategory(3);
        hotelOrderService.updateOrders(hotelOrder);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
