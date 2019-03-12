package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardTotalMoneyLog;
import com.busi.service.RewardTotalMoneyLogService;
import com.busi.utils.CommonUtils;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/***
 * 用户奖励总金额信息接口
 * author：stj
 * create time：2019-3-6 16:30:31
 */
@RestController
public class RewardTotalMoneyLogController extends BaseController implements RewardTotalMoneyLogApiController {

    @Autowired
    RewardTotalMoneyLogService rewardTotalMoneyLogService;

    @Autowired
    MqUtils mqUtils;

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

    @Override
    public ReturnData rewardMoneyToPurse(@Valid @RequestBody RewardTotalMoneyLog rewardTotalMoneyLog, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证权限
        if (CommonUtils.getMyId() != rewardTotalMoneyLog.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + rewardTotalMoneyLog.getUserId() + "]的奖励总金额信息", new JSONObject());
        }
        RewardTotalMoneyLog rtml = rewardTotalMoneyLogService.findRewardTotalMoneyLogInfo(rewardTotalMoneyLog.getUserId());
        if(rtml==null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您当前没有奖励可以转入钱包", rewardTotalMoneyLog);
        }
        if(rtml.getRewardTotalMoney()<50){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您当前奖励金额不满50元，暂时无法转入钱包", rewardTotalMoneyLog);
        }
        if(rewardTotalMoneyLog.getRewardTotalMoney()>rtml.getRewardTotalMoney()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您要转入钱包的金额大于您的奖励余额，无法转入钱包", rewardTotalMoneyLog);
        }
        if(rewardTotalMoneyLog.getRewardTotalMoney()<50){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您要转入钱包的金额小于50元，暂时无法转入钱包", rewardTotalMoneyLog);
        }
        //开始转入
        //更新奖励系统余额
        double money = rtml.getRewardTotalMoney()-rewardTotalMoneyLog.getRewardTotalMoney();
        if(money<0){//异常数据处理 防止出现负数 也可直接返回服务端异常
            money=0;
        }
        rtml.setRewardTotalMoney(money);
        rewardTotalMoneyLogService.update(rtml);
        //更新钱包
        mqUtils.sendPurseMQ(rtml.getUserId(),21,0,rewardTotalMoneyLog.getRewardTotalMoney());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
