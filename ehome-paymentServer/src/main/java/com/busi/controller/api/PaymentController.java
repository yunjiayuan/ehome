package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PursePayPassword;
import com.busi.entity.ReturnData;
import com.busi.entity.UserBankCardInfo;
import com.busi.service.PursePayPasswordService;
import com.busi.service.UserBankCardInfoService;
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
 * 支付相关接口
 * author：SunTianJie
 * create time：2018/8/23 9:55
 */
@RestController
public class PaymentController extends BaseController implements PaymentApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private UserBankCardInfoService userBankCardInfoService;

    @Autowired
    private PursePayPasswordService pursePayPasswordService;
    /***
     * 获取私钥  一次一密，10分钟有效，使用后失效，只能使用一次
     * @return
     */
    @Override
    public ReturnData getPaymentKey() {
        String payKey = CommonUtils.strToMD5(CommonUtils.getToken()+CommonUtils.getClientId()+new Date().getTime()+CommonUtils.getRandom(6,0), 16);//支付秘钥key
        redisUtils.set(Constants.REDIS_KEY_PAYMENT_PAYKEY+CommonUtils.getMyId(),payKey,Constants.MSG_TIME_OUT_MINUTE_10);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("paymentKey",payKey);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 查询支付密码设置信息和银行卡绑定信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findPayPasswordInfo(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,无权限进行此操作",new JSONObject());
        }
        int payPwdStatus = 0;//0表示该账号尚未设置过支付密码，1表示已设置过支付密码
        int bindBankCardStatus = 0;//0银行卡未绑定，1表示银行卡已绑定
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+userId );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword pursePayPassword = null;
            //缓存中没有用户对象信息 查询数据库
            pursePayPassword = pursePayPasswordService.findPursePayPassword(userId);
            if(pursePayPassword==null){
                pursePayPassword = new PursePayPassword();
                pursePayPassword.setUserId(userId);
            }else{
                payPwdStatus=1;
                pursePayPassword.setRedisStatus(1);
            }
            //更新缓存
            payPasswordMap = CommonUtils.objectToMap(pursePayPassword);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+userId,payPasswordMap,Constants.USER_TIME_OUT);
        }else{
            if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==1) {//数据库中有对应记录
                payPwdStatus=1;
            }
        }
        Map<String,Object> bankMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_BANKCARD+userId );
        if(bankMap==null||bankMap.size()<=0){
            UserBankCardInfo userBankCardInfo = null;
            //缓存中没有用户对象信息 查询数据库
            userBankCardInfo = userBankCardInfoService.findUserBankCardInfo(userId);
            if(userBankCardInfo==null){
                userBankCardInfo = new UserBankCardInfo();
                userBankCardInfo.setUserId(userId);
            }else{
                bindBankCardStatus=1;
                userBankCardInfo.setRedisStatus(1);
            }
            //更新缓存
            bankMap = CommonUtils.objectToMap(userBankCardInfo);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_BANKCARD+userId,bankMap,Constants.USER_TIME_OUT);
        }else{
            if(Integer.parseInt(bankMap.get("redisStatus").toString())==1) {//数据库中有对应记录
                bindBankCardStatus=1;
            }
        }
        //响应客户端
        Map<String,Object> map = new HashMap<>();
        map.put("payPwdStatus",payPwdStatus);
        map.put("bindBankCardStatus",bindBankCardStatus);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
