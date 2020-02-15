package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.EpidemicSituation;
import com.busi.entity.ReturnData;
import com.busi.service.EpidemicSituationService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/***
 * 疫情本地新增接口
 * author：zhaojiajie
 * create time：2020-02-15 10:40:23
 */
@RestController
public class EpidemicSituationLController extends BaseController implements EpidemicSituationLocalController {

    @Autowired
    EpidemicSituationService epidemicSituationService;

    /***
     * 新增
     * @param epidemicSituation
     * @return
     */
    @Override
    public ReturnData addEpidemicSituation(@RequestBody EpidemicSituation epidemicSituation) {
        epidemicSituationService.add(epidemicSituation);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
