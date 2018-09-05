package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Pay;
import com.busi.entity.ReturnData;
import com.busi.utils.Constants;
import com.busi.utils.MqUtils;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 支付--购买会员业务
 * author：SunTianJie
 * create time：2018/8/28 15:21
 */
@Service
public class MemberOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay,Map<String,Object> purseMap) {

        //获取未支付的订单
        Map<String,Object> memberOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_MEMBER+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(memberOrderMap==null||memberOrderMap.size()<=0||Integer.parseInt(memberOrderMap.get("payStatus").toString())!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致购买会员失败，请重新购买",new JSONObject());
        }
        //判断余额
        long money = Long.parseLong(memberOrderMap.get("money").toString());//将要花费的钱
        double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买会员操作",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_MEMBER+pay.getUserId()+"_"+pay.getOrderNumber(),"payStatus",1);

        //判断购买会员类型
        switch (pay.getServiceType()) {//将要购买的会员类型

            case 6://购买创始元老级会员支付

                break;
            case  9://购买元老级会员支付

                break;
            case 10://购买普通会员支付

                break;
            case 11://购买VIP高级会员支付

                break;
            default:
                break;
        }

        //开始扣款支付
        mqUtils.sendPurseMQ(pay.getUserId(),17,0,money);//家币转入
        //回调业务 更新会员状态

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
