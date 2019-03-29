package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.GoodNumber;
import com.busi.entity.ReturnData;
import com.busi.service.GoodNumberService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 预售靓号相关业务接口(内部调用)
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
@RestController
public class GoodNumberLController extends BaseController implements GoodNumberLocalController {

    @Autowired
    GoodNumberService goodNumberService;

    /***
     * 新增靓号门牌号
     * @param goodNumber
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addGoodNumber(@Valid  @RequestBody GoodNumber goodNumber, BindingResult bindingResult) {
        goodNumberService.add(goodNumber);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新靓号状态(目前做成删除已购买的靓号)
     * @param goodNumber
     * @return
     */
    @Override
    public ReturnData updateGoodNumber( @RequestBody GoodNumber goodNumber) {
        GoodNumber gn = goodNumberService.findGoodNumberInfo(goodNumber.getProId(),goodNumber.getHouse_number());
        if(gn==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "更新靓号["+goodNumber.getProId()+"_"+goodNumber.getHouse_number()+"]状态失败,该靓号不存在", new JSONObject());
        }
        gn.setStatus(1);
        int count = goodNumberService.updateStatus(goodNumber);
        if(count<=0){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "更新靓号["+goodNumber.getProId()+"_"+goodNumber.getHouse_number()+"]状态失败", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
