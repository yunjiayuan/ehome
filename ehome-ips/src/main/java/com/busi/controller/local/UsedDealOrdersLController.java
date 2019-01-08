package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UsedDealOrders;
import com.busi.service.UsedDealOrdersService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 二手订单
 * @author: ZHaoJiaJie
 * @create: 2019-01-08 17:16
 */
@RestController
public class UsedDealOrdersLController extends BaseController implements UsedDealOrdersLocalController {

    @Autowired
    UsedDealOrdersService usedDealOrdersService;

    @Override
    public ReturnData updatePayType(@RequestBody UsedDealOrders usedDealOrders) {

        usedDealOrdersService.updatePayType(usedDealOrders);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
