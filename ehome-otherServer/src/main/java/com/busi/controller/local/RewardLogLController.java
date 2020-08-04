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
        if(rewardLog.getInfoId()>0){//更新生活圈钱包记录
            //检测之前是否已存在
            RewardLog rl = rewardLogService.findRewardLog(rewardLog.getUserId(),rewardLog.getRewardType(),rewardLog.getInfoId());
            if(rl!=null){//已存在
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "奖励记录已存在，不再新增", new JSONObject());
            }
        }
        rewardLog.setTime(new Date());
        if(rewardLog.getRewardType()==0||rewardLog.getRewardType()==1){// 0红包雨奖励 1新人注册奖励 不做新提醒
            rewardLog.setIsNew(1);//已读
        }
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
