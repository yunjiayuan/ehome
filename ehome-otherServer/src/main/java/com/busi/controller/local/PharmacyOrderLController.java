package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PharmacyOrder;
import com.busi.entity.ReturnData;
import com.busi.service.PharmacyOrderService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 家门口药店订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 17:06:12
 */
@RestController
public class PharmacyOrderLController extends BaseController implements PharmacyOrderLocalController {

    @Autowired
    PharmacyOrderService pharmacyOrderService;

    /**
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @Override
    public ReturnData updatePayState(@RequestBody PharmacyOrder hotelOrder) {
        hotelOrder.setUpdateCategory(4);
        pharmacyOrderService.updateOrders(hotelOrder);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
