package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.ReturnData;
import com.busi.service.HomeHospitalRecordService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 医生咨询订单
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 14:35:59
 */
@RestController
public class HospitalOrdersLController extends BaseController implements HospitalOrdersLocalController {

    @Autowired
    HomeHospitalRecordService homeHospitalService;

    /**
     * @param homeHospitalRecord
     * @Description: 更新订单支付状态
     * @Param: homeHospitalRecord
     * @return:
     */
    @Override
    public ReturnData updatePayStates(@RequestBody HomeHospitalRecord homeHospitalRecord) {
        homeHospitalRecord.setPayState(homeHospitalRecord.getPayState());
        homeHospitalService.updateOrders(homeHospitalRecord);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
