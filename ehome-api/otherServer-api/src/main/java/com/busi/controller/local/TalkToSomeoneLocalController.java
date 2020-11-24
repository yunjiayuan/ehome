package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.TalkToSomeoneOrder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 找人倾诉支付相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 13:45:21
 */
public interface TalkToSomeoneLocalController {
    /**
     * @Description: 更新订单支付状态
     * @Param: hotelOrder
     * @return:
     */
    @PutMapping("updateSomeoneOrderPayState")
    ReturnData updatePayState(@RequestBody TalkToSomeoneOrder hotelOrder);
}
