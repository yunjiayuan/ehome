package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserBankCardInfo;
import com.busi.service.UserBankCardInfoService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付绑定银行卡相关接口
 * author：SunTianJie
 * create time：2018/8/24 16:06
 */
@RestController
public class UserBankCardInfoController extends BaseController implements UserBankCardInfoApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private UserBankCardInfoService userBankCardInfoService;

    /***
     * 新增银行卡绑定信息
     * @param userBankCardInfo
     * @return
     */
    @Override
    public ReturnData addUserBankCardInfo(@Valid @RequestBody UserBankCardInfo userBankCardInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userBankCardInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userBankCardInfo.getUserId()+"]的银行卡信息",new JSONObject());
        }
        //判断当前用户是否已绑定过银行卡
        Map<String,Object> bankMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_BANKCARD+userBankCardInfo.getUserId() );
        if(bankMap==null||bankMap.size()<=0){
            UserBankCardInfo ubci = null;
            //缓存中没有用户对象信息 查询数据库
            ubci = userBankCardInfoService.findUserBankCardInfo(userBankCardInfo.getUserId());
            if(ubci!=null){
                return returnData(StatusCode.CODE_BANKCARD_IS_EXIST_ERROR.CODE_VALUE,"您绑定过银行卡",new JSONObject());
            }
        }else{
            if(Integer.parseInt(bankMap.get("redisStatus").toString())==1) {//redisStatus==1 说明数据中已有此记录
                return returnData(StatusCode.CODE_BANKCARD_IS_EXIST_ERROR.CODE_VALUE,"您绑定过银行卡",new JSONObject());
            }
        }
        //验证该银行卡信息是否正确
        UserBankCardInfo ubci = null;
        ubci = userBankCardInfoService.findUserBankCardInfoByBankCard(userBankCardInfo.getBankCard());
        if(ubci!=null){//本地库中已存在
            //根据本地库中的银行卡信息验证该银行卡信息是否正确
            if(!ubci.getBankCardNo().equals(userBankCardInfo.getBankCardNo())||!ubci.getBankName().equals(userBankCardInfo.getBankName())
                    ||!ubci.getBankPhone().equals(userBankCardInfo.getBankPhone())){
                return returnData(StatusCode.CODE_BANKCARD_CHECK_ERROR.CODE_VALUE,"您填写的银行卡信息与该卡在银行中预留的信息不符!",new JSONObject());
            }
        }else{//本地库中不存在
            //远程验证该银行卡信息是否正确
            ubci = RealNameUtils.checkBankCard(userBankCardInfo.getUserId(),userBankCardInfo.getBankCard(),userBankCardInfo.getBankName(),
                    userBankCardInfo.getBankCardNo(),userBankCardInfo.getBankPhone());
            if(ubci==null){
                return returnData(StatusCode.CODE_BANKCARD_CHECK_ERROR.CODE_VALUE,"您填写的银行卡信息与该卡在银行中预留的信息不符!",new JSONObject());
            }
        }
        //开始绑定新银行卡
        userBankCardInfo.setTime(new Date());
        userBankCardInfoService.addUserBankCardInfo(userBankCardInfo);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_BANKCARD+userBankCardInfo.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 验证提交的银行卡信息与该用户绑定银行卡信息是否一致
     * @return 返回 私钥 用于重置支付密码的凭据
     */
    @Override
    public ReturnData checkUserBankCardInfo(@Valid @RequestBody UserBankCardInfo userBankCardInfo, BindingResult bindingResult){
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证权限
        if(CommonUtils.getMyId()!=userBankCardInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限验证用户["+userBankCardInfo.getUserId()+"]的银行卡信息",new JSONObject());
        }
        //判断当前用户是否已绑定过银行卡
        Map<String,Object> bankMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_BANKCARD+userBankCardInfo.getUserId());
        if(bankMap==null||bankMap.size()<=0){
            UserBankCardInfo ubci = null;
            //缓存中没有用户对象信息 查询数据库
            ubci = userBankCardInfoService.findUserBankCardInfo(userBankCardInfo.getUserId());
            if(ubci==null){
                return returnData(StatusCode.CODE_BANKCARD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未绑定过银行卡，无法设置支付密码!",new JSONObject());
            }
            bankMap = CommonUtils.objectToMap(ubci);
        }else{
            if(Integer.parseInt(bankMap.get("redisStatus").toString())==0) {//redisStatus==0 说明数据中无此记录
                return returnData(StatusCode.CODE_BANKCARD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未绑定过银行卡，无法设置支付密码!",new JSONObject());
            }
        }
        //开始验证卡信息是否正确
        if(!userBankCardInfo.getBankCard().equals(bankMap.get("bankCard").toString())||!userBankCardInfo.getBankPhone().equals(bankMap.get("bankPhone").toString())
                ||!userBankCardInfo.getBankName().equals(bankMap.get("bankName").toString())||!userBankCardInfo.getBankCardNo().equals(bankMap.get("bankCardNo").toString())){
            return returnData(StatusCode.CODE_BANKCARD_CHECK_ERROR.CODE_VALUE,"您填写的银行卡信息与该卡在银行中预留的信息不符!",new JSONObject());
        }
        //生成私钥 用于后边重置支付密码接口使用
        String payKey = CommonUtils.strToMD5(CommonUtils.getToken()+CommonUtils.getClientId()+new Date().getTime()+CommonUtils.getRandom(6,0), 16);//支付秘钥key
        redisUtils.set(Constants.REDIS_KEY_PAYMENT_PAYKEY+CommonUtils.getMyId(),payKey,Constants.MSG_TIME_OUT_MINUTE_10);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("paymentKey",payKey);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 找回密码流程中 验证手机验证码是否正确
     * @param code       手机验证码
     * @param paymentKey 私钥（银行卡验证接口中返回的私钥信息）
     * @return
     */
    @Override
    public ReturnData checkCodeByFindPayPassword(@PathVariable String code,@PathVariable String paymentKey) {
        //验证参数格式
        if(CommonUtils.checkFull(code)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"code参数有误",new JSONObject());
        }
        if(CommonUtils.checkFull(paymentKey)||paymentKey.length()!=16){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"paymentKey参数有误",new JSONObject());
        }
        //添加防暴力验证
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的验证码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服",new JSONObject());
        }
        //验证修改秘钥是否正确
        Object serverKey = redisUtils.getKey(Constants.REDIS_KEY_PAYMENT_PAYKEY+CommonUtils.getMyId());
        if(serverKey==null){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1);
            }
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE,"操作已过期，秘钥已过期，请重新验证银行卡信息找回密码!",new JSONObject());
        }
        if(!paymentKey.equals(serverKey.toString())){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1);
            }
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE,"秘钥不正确，请重新验证银行卡信息找回密码!",new JSONObject());
        }
        //验证手机验证码是否正确
        String serverCode = (String)redisUtils.getKey(Constants.REDIS_KEY_PAY_FIND_PAYPASSWORD_CODE+CommonUtils.getMyId());
        if(CommonUtils.checkFull(serverCode)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码已过期,请重新获取",new JSONObject());
        }
        if(!serverCode.equals(code)){//不相等
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您输入的验证码有误,请重新输入",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
