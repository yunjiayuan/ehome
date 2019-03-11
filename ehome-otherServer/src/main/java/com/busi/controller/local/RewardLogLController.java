package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardLog;
import com.busi.entity.RewardTotalMoneyLog;
import com.busi.service.RewardLogService;
import com.busi.service.RewardTotalMoneyLogService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

    @Autowired
    RewardTotalMoneyLogService rewardTotalMoneyLogService;

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
        //更新总金额
        RewardTotalMoneyLog rewardTotalMoneyLog = rewardTotalMoneyLogService.findRewardTotalMoneyLogInfo(userId);
        if(rewardTotalMoneyLog==null){//不存在 新增
            rewardTotalMoneyLog = new RewardTotalMoneyLog();
            rewardTotalMoneyLog.setUserId(rewardLog.getUserId());
            rewardTotalMoneyLog.setRewardTotalMoney(rewardLog.getRewardMoney());
            rewardTotalMoneyLogService.add(rewardTotalMoneyLog);
        }else{//存在 更新
            rewardTotalMoneyLog.setRewardTotalMoney(rewardTotalMoneyLog.getRewardTotalMoney()+rewardLog.getRewardMoney());
            rewardTotalMoneyLogService.update(rewardTotalMoneyLog);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
