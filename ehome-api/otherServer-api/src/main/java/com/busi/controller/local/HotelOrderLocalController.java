package com.busi.controller.local;

import com.busi.entity.HotelOrder;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 家门口酒店民宿订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 17:06:12
 */
public interface HotelOrderLocalController {
    /**
     * @Description: 更新订单支付状态
     * @Param: hotelOrder
     * @return:
     */
    @PutMapping("updateHotelOrderPayState")
    ReturnData updatePayState(@RequestBody HotelOrder hotelOrder);
}
