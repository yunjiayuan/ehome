package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardTotalMoneyLog;
import com.busi.service.RewardTotalMoneyLogService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/***
 * 用户奖励总金额信息相关接口（内部服务器间调用）
 * author：stj
 * create time：2019-3-6 16:30:31
 */
@RestController
public class RewardTotalMoneyLogLController extends BaseController implements RewardTotalMoneyLogLocalController {

    @Autowired
    RewardTotalMoneyLogService rewardTotalMoneyLogService;

    /***
     * 修改奖励总金额记录 无记录时新增
     * @param rewardTotalMoneyLog
     * @return
     */
    @Override
    public ReturnData updateTotalRewardMoney(@RequestBody RewardTotalMoneyLog rewardTotalMoneyLog) {
        long userId = rewardTotalMoneyLog.getUserId();
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        RewardTotalMoneyLog rtml = rewardTotalMoneyLogService.findRewardTotalMoneyLogInfo(userId);
        if(rtml==null){//不存在 新增
            rewardTotalMoneyLogService.add(rewardTotalMoneyLog);
        }else{//存在 更新
            rewardTotalMoneyLogService.update(rewardTotalMoneyLog);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
