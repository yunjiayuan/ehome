package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardTotalMoneyLog;
import com.busi.service.RewardTotalMoneyLogService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/***
 * 用户奖励总金额信息接口
 * author：stj
 * create time：2019-3-6 16:30:31
 */
@RestController
public class RewardTotalMoneyLogController extends BaseController implements RewardTotalMoneyLogApiController {

    @Autowired
    RewardTotalMoneyLogService rewardTotalMoneyLogService;

    /***
     * 根据指定用户，查询获得奖励的总金额
     * @param userId
     * @return
     */
    @Override
    public ReturnData findTotalRewardMoney(@PathVariable long userId) {
        //验证权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userId + "]的奖励总金额信息", new JSONObject());
        }
        RewardTotalMoneyLog rewardTotalMoneyLog = rewardTotalMoneyLogService.findRewardTotalMoneyLogInfo(userId);
        if(rewardTotalMoneyLog==null){
            rewardTotalMoneyLog = new RewardTotalMoneyLog();
            rewardTotalMoneyLog.setUserId(userId);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", rewardTotalMoneyLog);
    }
}
