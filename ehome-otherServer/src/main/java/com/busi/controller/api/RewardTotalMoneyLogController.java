package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.RewardTotalMoneyLog;
import com.busi.entity.UserInfo;
import com.busi.service.RewardTotalMoneyLogService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;


    /***
     * 根据指定用户，查询获得奖励的总金额
     * @param userId
     * @return
     */
    @Override
    public ReturnData findTotalRewardMoney(@PathVariable long userId) {
        //验证权限
        long myId = CommonUtils.getMyId();
        if(CommonUtils.getAdministrator(myId,redisUtils)<1){
            if (myId != userId) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userId + "]的奖励总金额信息", new JSONObject());
            }
        }
        Map<String, Object> rewardTotalMoneyLogMap = redisUtils.hmget(Constants.REDIS_KEY_REWARD_TOTAL_MONEY + userId);
        if (rewardTotalMoneyLogMap == null || rewardTotalMoneyLogMap.size() <= 0) {
            RewardTotalMoneyLog rewardTotalMoneyLog = rewardTotalMoneyLogService.findRewardTotalMoneyLogInfo(userId);
            if(rewardTotalMoneyLog==null){
                rewardTotalMoneyLog = new RewardTotalMoneyLog();
                rewardTotalMoneyLog.setUserId(userId);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", rewardTotalMoneyLog);
            }
            //放入缓存
            rewardTotalMoneyLogMap = CommonUtils.objectToMap(rewardTotalMoneyLog);
            redisUtils.hmset(Constants.REDIS_KEY_REWARD_TOTAL_MONEY + userId, rewardTotalMoneyLogMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", rewardTotalMoneyLogMap);

    }

    /***
     * 查询奖励总金额列表
     * @param userId 被查询人的用户ID  -1查询所有人
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findTotalRewardMoneyList(@PathVariable long userId,@PathVariable int page,@PathVariable int count) {
        //判断分页参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //验证权限
        long myId = CommonUtils.getMyId();
        if(CommonUtils.getAdministrator(myId,redisUtils)<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作奖励总金额列表信息", new JSONObject());
        }
        PageBean<RewardTotalMoneyLog> pageBean;
        pageBean = rewardTotalMoneyLogService.findRewardTotalMoneyLogInfoList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if(list!=null&&list.size()>0){
            for (int i=0;i<list.size();i++){
                RewardTotalMoneyLog rewardTotalMoneyLog = (RewardTotalMoneyLog) list.get(i);
                if(rewardTotalMoneyLog!=null){
                    UserInfo userInfo = userInfoUtils.getUserInfo(rewardTotalMoneyLog.getUserId());
                    if(userInfo!=null){
                        rewardTotalMoneyLog.setUserName(userInfo.getName());
                        rewardTotalMoneyLog.setUserHead(userInfo.getHead());
                        rewardTotalMoneyLog.setHouseNumber(userInfo.getHouseNumber());
                        rewardTotalMoneyLog.setProId(userInfo.getProType());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
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
        if(rtml.getRewardTotalMoney()<Constants.REWARD_TOTAL_MONEY){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您当前奖励金额不满"+Constants.REWARD_TOTAL_MONEY+"元，暂时无法转入钱包", rewardTotalMoneyLog);
        }  
        if(rewardTotalMoneyLog.getRewardTotalMoney()>rtml.getRewardTotalMoney()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您要转入钱包的金额大于您的奖励余额，无法转入钱包", rewardTotalMoneyLog);
        }
        if(rewardTotalMoneyLog.getRewardTotalMoney()<Constants.REWARD_TOTAL_MONEY){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您要转入钱包的金额小于"+Constants.REWARD_TOTAL_MONEY+"元，暂时无法转入钱包", rewardTotalMoneyLog);
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
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_REWARD_TOTAL_MONEY + rewardTotalMoneyLog.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
