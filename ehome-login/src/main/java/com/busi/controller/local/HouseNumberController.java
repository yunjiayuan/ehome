package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.entity.HouseNumber;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.service.HouseNumberService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门牌号相关接口 Controller
 * author：SunTianJie
 * create time：2018/6/7 16:06
 */
@RestController //此处必须继承BaseController和实现项目对应的接口TestApiController
public class HouseNumberController extends BaseController implements HouseNumberLocalController {

    @Autowired
    HouseNumberService houseNumberService;

    /***
     * 更新门牌号记录接口
     * @param houseNumber
     * @return
     */
    @Override
    public ReturnData updateHouseNumber(@RequestBody HouseNumber houseNumber) {
        int count = houseNumberService.update(houseNumber);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新门牌号记录失败",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
