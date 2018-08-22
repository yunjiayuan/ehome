package com.busi.controller.local;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PurseChangingLog;
import com.busi.entity.ReturnData;
import com.busi.service.PurseChangingLogService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;

/**
 * 钱包明细相关接口(通过fegin本地内部调用)
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
@RestController
public class PurseChangingLogLController extends BaseController implements PurseChangingLogLocalController{

    @Autowired
    private PurseChangingLogService purseChangingLogService;

    /***
     * 新增用户账户交易明细接口
     * @param purseChangingLog
     * @return
     */
    @Override
    public ReturnData addPurseChangingLog(@Valid @RequestBody PurseChangingLog purseChangingLog) {
        //验证参数格式
//        if(bindingResult.hasErrors()){
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
//        }
        //验证修改人权限
//        if(CommonUtils.getMyId()!=purseChangingLog.getUserId()){
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限新增用户["+purseChangingLog.getUserId()+"]的钱包明细信息",new JSONObject());
//        }
        //开始新增
        purseChangingLog.setTime(new Date());
        purseChangingLogService.addPurseChangingLog(purseChangingLog);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
