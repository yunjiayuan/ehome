package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ExchangeOrder;
import com.busi.entity.Purse;
import com.busi.entity.PursePayPassword;
import com.busi.entity.ReturnData;
import com.busi.service.PurseInfoService;
import com.busi.service.PursePayPasswordService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 钱包相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
@RestController
public class PurseController extends BaseController implements PurseApiController{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PurseInfoService purseInfoService;

    @Autowired
    private PursePayPasswordService pursePayPasswordService;

    /***
     * 查询用户钱包信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findPurseInfo(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,无权限进行此操作",new JSONObject());
        }
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId );
        if(purseMap==null||purseMap.size()<=0){
            Purse purse = null;
            //缓存中没有用户对象信息 查询数据库
            purse = purseInfoService.findPurseInfo(userId);
            if(purse==null){
                purse = new Purse();
                purse.setUserId(userId);
            }else{
                purse.setRedisStatus(1);//数据库中已有对应记录
            }
            //更新缓存
            purseMap = CommonUtils.objectToMap(purse);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId,purseMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",purseMap);
    }


    /***
     * 查询互动用户双方钱包家点信息
     * @param myId     当前登录用户ID
     * @param userId   好友用户ID
     * @return
     */
    @Override
    public ReturnData findHomePointInfo(@PathVariable long myId,@PathVariable long userId) {
        //验证参数
        if(userId<=0||myId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"myId或userId参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=myId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,无权限进行此操作",new JSONObject());
        }
        Map<String,Object> myIdPurseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+myId );
        if(myIdPurseMap==null||myIdPurseMap.size()<=0){
            Purse myIdPurse = null;
            //缓存中没有用户对象信息 查询数据库
            myIdPurse = purseInfoService.findPurseInfo(myId);
            if(myIdPurse==null){
                myIdPurse = new Purse();
                myIdPurse.setUserId(userId);
            }else{
                myIdPurse.setRedisStatus(1);//数据库中已有对应记录
            }
            //更新缓存
            myIdPurseMap = CommonUtils.objectToMap(myIdPurse);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_PURSEINFO+myId,myIdPurseMap,Constants.USER_TIME_OUT);
        }
        Map<String,Object> userIdPurseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId );
        if(userIdPurseMap==null||userIdPurseMap.size()<=0){
            Purse userIdPurse = null;
            //缓存中没有用户对象信息 查询数据库
            userIdPurse = purseInfoService.findPurseInfo(userId);
            if(userIdPurse==null){
                userIdPurse = new Purse();
                userIdPurse.setUserId(userId);
            }else{
                userIdPurse.setRedisStatus(1);//数据库中已有对应记录
            }
            //更新缓存
            userIdPurseMap = CommonUtils.objectToMap(userIdPurse);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId,userIdPurseMap,Constants.USER_TIME_OUT);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("myHomePoint",Long.parseLong(myIdPurseMap.get("homePoint").toString()));
        map.put("inviteeHomePoint",Long.parseLong(userIdPurseMap.get("homePoint").toString()));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }


    /**
     * 钱包兑换--生成订单接口
     * @param userId       当前用户ID
     * @param exChangeType 兑换类型 1家币兑换家点（1:100） 2人民币兑换家币（1:1）
     * @param money        当exChangeType==1时人民币兑换家币 当exChangeType==2时家币兑换家点
     * @return
     */
    @Override
    public ReturnData exChange(@PathVariable long userId,@PathVariable int exChangeType,@PathVariable long money) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        if(exChangeType<1||exChangeType>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"exChangeType参数有误",new JSONObject());
        }
        if(money<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"money参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,无权限进行此操作",new JSONObject());
        }
        //检测账户信息
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId );
        if(purseMap==null||purseMap.size()<=0){
            Purse purse = null;
            //缓存中没有用户对象信息 查询数据库
            purse = purseInfoService.findPurseInfo(userId);
            if(purse==null){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行兑换操作",new JSONObject());
            }
            purseMap = CommonUtils.objectToMap(purse);
        }
        //检测是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+userId );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword pursePayPassword = null;
            //缓存中没有用户对象信息 查询数据库
            pursePayPassword = pursePayPasswordService.findPursePayPassword(userId);
            if(pursePayPassword==null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您当前账户尚未设置过支付密码，无法进行兑换操作",new JSONObject());
            }
        }
        //判断余额
        if(exChangeType==1){//人民币兑换家币
            double spareMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
            if(spareMoney<money){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行兑换操作",new JSONObject());
            }
        }else{//家币兑换家点
            long homePoint = Long.parseLong(purseMap.get("homePoint").toString());
            if(homePoint<money){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行兑换操作",new JSONObject());
            }
        }
        //生成兑换订单 等待支付
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setOrderNumber(CommonUtils.getOrderNumber(userId,Constants.REDIS_KEY_PAY_ORDER_EXCHANGE));
        exchangeOrder.setUserId(userId);
        exchangeOrder.setExchangeType(exChangeType);
        exchangeOrder.setMoney(money);
        exchangeOrder.setPayStatus(0);//未支付状态
        exchangeOrder.setTime(new Date());
        //将订单放入缓存中  5分钟有效时间  超时作废
        redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_EXCHANGE+userId+"_"+exchangeOrder.getOrderNumber(),CommonUtils.objectToMap(exchangeOrder),Constants.TIME_OUT_MINUTE_5);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("orderNumber",exchangeOrder.getOrderNumber());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
