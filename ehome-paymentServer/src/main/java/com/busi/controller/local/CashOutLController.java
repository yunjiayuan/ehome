package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.CashOutOrder;
import com.busi.entity.Purse;
import com.busi.entity.ReturnData;
import com.busi.service.CashOutService;
import com.busi.service.PurseInfoService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * 钱包相关接口(通过fegin本地内部调用)
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
@RestController
public class CashOutLController extends BaseController implements CashOutLocalController{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    CashOutService cashOutService;
    /***
     *  提现同步到微信或者支付宝
     * @param cashOutOrder
     * @return
     */
    @Override
    public ReturnData cashOutToOther(@RequestBody CashOutOrder cashOutOrder) {
        Map<String,Object> cashOutOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+cashOutOrder.getId() );
        if(cashOutOrderMap==null||cashOutOrderMap.size()<=0){//交由定时任务补偿系统
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        cashOutOrder = (CashOutOrder)CommonUtils.mapToObject(cashOutOrderMap,CashOutOrder.class);
        if(cashOutOrder==null){//交由定时任务补偿系统
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        //判断状态 防止重复操作
        if(cashOutOrder.getCashOutStatus()!=0){//已到账
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+cashOutOrder.getId() ,"cashOutStatus",1);
        if(cashOutOrder.getType()==0){//提现到微信
            TransfersDto model = new TransfersDto();
            model.setMch_appid(Constants.WEIXIN_MCH_APPID);
            model.setMchid(Constants.WEIXIN_MCHID);
            model.setMch_name(Constants.WEIXIN_MCH_NAME);
            model.setOpenid(cashOutOrder.getOpenid());
            model.setAmount(cashOutOrder.getMoney());
            model.setDesc("提现");
            int res = WechatpayUtil.doTransfers(model);
            if(res==0){
                cashOutOrder.setCashOutStatus(1);
                cashOutService.updateCashOutStatus(cashOutOrder);
                redisUtils.expire(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+cashOutOrder.getId() ,0);//清除
            }
        }else if(cashOutOrder.getType()==1){//提现到支付宝

        }else {//提现到银行卡

        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
