package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.PurseChangingLog;
import com.busi.entity.ReturnData;
import com.busi.service.PurseChangingLogService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钱包交易明细相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
@RestController
public class PurseChangingLogController extends BaseController implements PurseChangingLogApiController{

    @Autowired
    private PurseChangingLogService purseChangingLogService;


    /***
     * 查询用户钱包交易明细信息
     * @param userId    将要查询的用户ID
     * @param currencyType 交易支付类型 -1所有 0钱(真实人民币),1家币,2家点
     * @param tradeType 交易类型-1所有 0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出,18游戏支出，19游戏转入，20任务奖励转入
     * @param beginTime 查询的日期起始时间 格式为20170501
     * @param endTime   查询的日期结束时间 格式为20170530
     * @param page      页码 第几页 起始值1
     * @param count     每页条数
     * @return
     */
    @Override
    public ReturnData findPurseLogInfo(@PathVariable long userId,@PathVariable int tradeType,@PathVariable int currencyType,
                                       @PathVariable String beginTime,@PathVariable String endTime,
                                       @PathVariable int page,@PathVariable int count) {
        //验证参数
        if(tradeType<-1||tradeType>21){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"tradeType参数有误",new JSONObject());
        }
        if(currencyType<-1||currencyType>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"currencyType参数有误",new JSONObject());
        }
        if(!CommonUtils.checkFull(beginTime)&&beginTime.length()!=8){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"beginTime参数有误",new JSONObject());
        }
        if(!CommonUtils.checkFull(endTime)&&endTime.length()!=8){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"endTime参数有误",new JSONObject());
        }
        if(page<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        //验证身份
        long myId = CommonUtils.getMyId();
        if(myId!=10076&&myId!=12770&&myId!=9389&&myId!=9999&&myId!=13005&&myId!=12774&&myId!=13031&&myId!=12769&&myId!=12796&&myId!=10053){
            if(CommonUtils.getMyId()!=userId){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,您无权限进行此操作",new JSONObject());
            }
        }
        //开始查询
        PageBean<PurseChangingLog> pageBean;
        pageBean = purseChangingLogService.findPurseChangingLogList(userId,tradeType,currencyType,beginTime,endTime,page,count);
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }
}
