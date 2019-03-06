package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardLog;
import com.busi.service.RewardLogService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;

/***
 * 用户奖励记录相关接口(服务期间调用)
 * author：stj
 * create time：2019-3-6 16:30:31
 */
@RestController
public class RewardLogLController extends BaseController implements RewardLogLocalController {

    @Autowired
    RewardLogService rewardLogService;

    /***
     * 新增
     * @param rewardLog
     * @return
     */
    @Override
    public ReturnData addRewardLog(@RequestBody RewardLog rewardLog) {
        long userId = rewardLog.getUserId();
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        rewardLog.setTime(new Date());
        rewardLogService.add(rewardLog);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
