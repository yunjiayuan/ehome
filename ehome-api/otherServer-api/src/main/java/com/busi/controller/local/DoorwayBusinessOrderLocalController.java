package com.busi.controller.local;

import com.busi.entity.DoorwayBusinessOrder;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 家门口商家订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 13:45:21
 */
public interface DoorwayBusinessOrderLocalController {
    /**
     * @Description: 更新订单支付状态
     * @Param: hotelOrder
     * @return:
     */
    @PutMapping("updateBusinessOrderPayState")
    ReturnData updatePayState(@RequestBody DoorwayBusinessOrder hotelOrder);
}
