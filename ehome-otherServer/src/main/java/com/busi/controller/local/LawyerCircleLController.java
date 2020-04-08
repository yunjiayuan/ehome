package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.LawyerCircleRecord;
import com.busi.entity.ReturnData;
import com.busi.service.HomeHospitalRecordService;
import com.busi.service.LawyerCircleService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 律师咨询订单
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 14:35:53
 */
@RestController
public class LawyerCircleLController extends BaseController implements LawyerCircleLocalController {

    @Autowired
    LawyerCircleService homeHospitalService;

    /**
     * @param lawyerCircleRecord
     * @Description: 更新订单支付状态
     * @Param: lawyerCircleRecord
     * @return:
     */
    @Override
    public ReturnData updatePayStates(@RequestBody LawyerCircleRecord lawyerCircleRecord) {
        lawyerCircleRecord.setPayState(lawyerCircleRecord.getPayState());
        homeHospitalService.updateOrders(lawyerCircleRecord);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
