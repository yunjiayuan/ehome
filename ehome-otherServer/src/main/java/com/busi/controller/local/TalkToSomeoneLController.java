package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.controller.api.TalkToSomeoneApiController;
import com.busi.entity.ReturnData;
import com.busi.entity.TalkToSomeoneOrder;
import com.busi.service.TalkToSomeoneService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 找人倾诉支付相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 13:48:59
 */
@RestController
public class TalkToSomeoneLController extends BaseController implements TalkToSomeoneLocalController {

    @Autowired
    TalkToSomeoneService hotelOrderService;

    /**
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @Override
    public ReturnData updatePayState(@RequestBody TalkToSomeoneOrder hotelOrder) {
        hotelOrderService.updateOrders(hotelOrder);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
