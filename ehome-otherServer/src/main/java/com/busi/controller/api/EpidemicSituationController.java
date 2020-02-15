package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationAbout;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.EpidemicSituationService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

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

    /***
     * 新增我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @Override
    public ReturnData addESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult) {

        EpidemicSituationAbout situationAbout = epidemicSituationService.findESabout(epidemicSituationAbout.getUserId());
        if (situationAbout != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }

        epidemicSituationAbout.setAddTime(new Date());
        epidemicSituationService.addESabout(epidemicSituationAbout);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 编辑我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @Override
    public ReturnData changeESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult) {
        epidemicSituationService.changeESabout(epidemicSituationAbout);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询我和疫情信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findESabout(@PathVariable long userId) {
        EpidemicSituationAbout situationAbout = epidemicSituationService.findESabout(userId);
        if (situationAbout != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", situationAbout);
    }
}
