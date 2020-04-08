package com.busi.controller.local;

import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 医生咨询订单
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 14:35:59
 */
public interface HospitalOrdersLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: homeHospitalRecord
     * @return:
     */
    @PutMapping("updateHospitalPayStates")
    ReturnData updatePayStates(@RequestBody HomeHospitalRecord homeHospitalRecord);
}
