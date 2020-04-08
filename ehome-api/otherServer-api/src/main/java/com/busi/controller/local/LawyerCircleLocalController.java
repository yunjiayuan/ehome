package com.busi.controller.local;

import com.busi.entity.LawyerCircleRecord;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 律师咨询订单
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 14:35:53
 */
public interface LawyerCircleLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: lawyerCircleRecord
     * @return:
     */
    @PutMapping("updateLvShiPayStates")
    ReturnData updatePayStates(@RequestBody LawyerCircleRecord lawyerCircleRecord);
}
