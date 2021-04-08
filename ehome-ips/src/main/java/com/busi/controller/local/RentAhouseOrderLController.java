package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.RentAhouse;
import com.busi.entity.RentAhouseOrder;
import com.busi.entity.ReturnData;
import com.busi.service.RentAhouseOrderService;
import com.busi.service.RentAhouseService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 租房买房订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2021-03-30 15:17:36
 */
@RestController
public class RentAhouseOrderLController extends BaseController implements RentAhouseOrderLocalController {

    @Autowired
    RentAhouseService rentAhouseService;

    @Autowired
    RentAhouseOrderService rentAhouseOrderService;

    /**
     * @param rentAhouseOrder
     * @Description: 更新订单支付状态
     * @return:
     */
    @Override
    public ReturnData updatePayType(@RequestBody RentAhouseOrder rentAhouseOrder) {
        rentAhouseOrderService.updatePayType(rentAhouseOrder);
        //更新房源状态为已出租
        RentAhouse sa = new RentAhouse();
        sa.setSellState(1);
        sa.setId(rentAhouseOrder.getHouseId());
        rentAhouseService.changeCommunityState(sa);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
