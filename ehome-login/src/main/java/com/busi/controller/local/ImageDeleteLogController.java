package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ImageDeleteLog;
import com.busi.entity.ReturnData;
import com.busi.service.ImageDeleteLogService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 图片相关接口
 * author：SunTianJie
 * create time：2018/8/2 8:09
 */
@RestController
public class ImageDeleteLogController extends BaseController implements ImageDeleteLogLocalController {

    @Autowired
    ImageDeleteLogService imageDeleteLogService;

    /***
     * 新增将要删除的图片记录
     * @param imageDeleteLog
     * @return
     */
    @Override
    public ReturnData addImageDeleteLog(@Valid  @RequestBody ImageDeleteLog imageDeleteLog) {
        //验证参数格式
//        if(bindingResult.hasErrors()){
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
//        }
        //开始添加
        int count = imageDeleteLogService.add(imageDeleteLog);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"添加将要删除的图片到记录表操作失败",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
