package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.ScenicSpotOrder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 家门口旅游订单
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 13:27:36
 */
public interface TravelOrderLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: scenicSpotOrder
     * @return:
     */
    @PutMapping("updateTravelOrderPayState")
    ReturnData updatePayState(@RequestBody ScenicSpotOrder scenicSpotOrder);
}
