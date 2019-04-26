package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HourlyWorkerOrders;
import com.busi.entity.ReturnData;
import com.busi.service.HourlyWorkerOrdersService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 小时工订单
 * @author: ZHaoJiaJie
 * @create: 2019-04-26 11:42
 */
@RestController
public class HourlyWorkerOrdersLController extends BaseController implements HourlyWorkerOrdersLocalController{

    @Autowired
    HourlyWorkerOrdersService hourlyWorkerOrdersService;

    /**
     * @program: ehome
     * @description: 小时工订单
     * @author: ZHaoJiaJie
     * @create: 2019-4-26 11:46:24
     */
    @Override
    public ReturnData updatePayState(@RequestBody HourlyWorkerOrders hourlyWorkerOrders) {

        hourlyWorkerOrders.setUpdateCategory(5);
        hourlyWorkerOrdersService.updateOrders(hourlyWorkerOrders);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
