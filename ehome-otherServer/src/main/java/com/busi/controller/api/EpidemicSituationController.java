package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.busi.controller.BaseController;
import com.busi.entity.EpidemicSituation;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.EpidemicSituationService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/***
 * 疫情相关接口
 * author：zhaojiajie
 * create time：2020-02-15 10:40:23
 */
@RestController
public class EpidemicSituationController extends BaseController implements EpidemicSituationApiController {

    @Autowired
    EpidemicSituationService epidemicSituationService;

    /***
     * 查询疫情
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findEpidemicSituation(@PathVariable int page, @PathVariable int count) {
        //开始查询
        PageBean<EpidemicSituation> pageBean = null;
        pageBean = epidemicSituationService.findList(page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
