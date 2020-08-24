package com.busi.controller.local;

import com.busi.entity.HotelOrder;
import com.busi.entity.HotelTourismBookedOrders;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 家门口酒店、景区订座订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-24 13:21:18
 */
public interface HotelTourismBookedOrdersLocalController {
    /**
     * @Description: 更新订单支付状态
     * @Param: hotelOrder
     * @return:
     */
    @PutMapping("updateHotelTourismBookedOrderPayState")
    ReturnData updatePayState(@RequestBody HotelTourismBookedOrders hotelTourismBookedOrders);
}
