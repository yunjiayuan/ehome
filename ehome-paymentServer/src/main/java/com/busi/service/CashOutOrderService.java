package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.CashOutOrder;
import com.busi.entity.Pay;
import com.busi.entity.ReturnData;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 提现业务
 * author：SunTianJie
 * create time：2020-7-2 08:34:39
 */
@Service
public class CashOutOrderService extends BaseController implements PayBaseService{

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    CashOutService cashOutService;

    /***
     * 修改提现支付状态
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {
        //获取缓存中的订单
        Map<String,Object> cashOutOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+pay.getOrderNumber() );
        CashOutOrder cashOutOrder = null;
        switch (pay.getServiceType()) {
            case 22://提现到微信
                if(cashOutOrderMap==null||cashOutOrderMap.size()<=0){
                    return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该提现操作已过期，请重新提现",new JSONObject());
                }
                cashOutOrder = (CashOutOrder)CommonUtils.mapToObject(cashOutOrderMap,CashOutOrder.class);
                if(cashOutOrder==null){
                    return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"该提现操作已过期，请重新进行提现",new JSONObject());
                }
                //判断余额
                double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
                if(serverMoney<cashOutOrder.getMoney()){
                    return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行提现",new JSONObject());
                }
                if(cashOutOrder.getPayStatus()!=0){//已支付
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"该提现已成功，无法再进行重复提现",new JSONObject());
                }
                //当前用户是否有权限
                if(pay.getUserId()!=cashOutOrder.getUserId()){
                    return returnData(StatusCode.CODE_RED_PACKETS_NOT_AWARDYOU.CODE_VALUE,"您无权使用其他人的账号进行提现操作",new JSONObject());
                }
                //更改状态 防止重复支付
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+pay.getOrderNumber() ,"payStatus",1);
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),37,0,cashOutOrder.getMoney()*-1);
                //回调业务 更新会员状态
                cashOutOrder.setPayStatus(1);//已支付
                cashOutService.addCashOutOrder(cashOutOrder);
                redisUtils.expire(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+pay.getOrderNumber(),0);//设置过期0秒
                //将提现申请 交由MQ异步处理 同步到微信
                TransfersDto model = new TransfersDto();
                model.setMch_appid(Constants.WEIXIN_MCH_APPID);
                model.setMchid(Constants.WEIXIN_MCHID);
                model.setMch_name(Constants.WEIXIN_MCH_NAME);
                model.setOpenid(cashOutOrder.getOpenid());
                model.setAmount(cashOutOrder.getMoney());
                model.setDesc("提现");
                WechatpayUtil.doTransfers(model);
                break;
            case 23://提现到支付宝

                break;
            case 24://提现到银行卡

                break;
            default:
                break;
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
