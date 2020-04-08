package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.ConsultationOrders;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 律师医生咨询订单
 * @author: ZHaoJiaJie
 * @create: 2019-04-26 11:40
 */
public interface ConsultationOrdersLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: consultationOrders
     * @return:
     */
    @PutMapping("updateConsultationPayStates")
    ReturnData updatePayStates(@RequestBody ConsultationOrders consultationOrders);
}
