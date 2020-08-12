package com.busi.controller.local;

import com.busi.entity.PharmacyOrder;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 家门口买药订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-10 15:07:58
 */
public interface PharmacyOrderLocalController {
    /**
     * @Description: 更新订单支付状态
     * @Param: pharmacyOrder
     * @return:
     */
    @PutMapping("updatePharmacyOrderPayState")
    ReturnData updatePayState(@RequestBody PharmacyOrder pharmacyOrder);
}
