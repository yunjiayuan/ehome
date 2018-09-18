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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Map;

/**
 * 支付密码相关接口
 * author：SunTianJie
 * create time：2018/8/24 16:06
 */
@RestController
public class PursePayPasswordController extends BaseController implements PursePayPasswordApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private PursePayPasswordService pursePayPasswordService;

    @Autowired
    private UserBankCardInfoService userBankCardInfoService;

    /***
     * 设置支付密码
     * @param pursePayPassword
     * @return
     */
    @Override
    public ReturnData addPursePayPassword(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=pursePayPassword.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+pursePayPassword.getUserId()+"]的支付密码信息",new JSONObject());
        }
        //验证之前是否绑定过银行卡
        Map<String,Object> bankMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_BANKCARD+pursePayPassword.getUserId() );
        if(bankMap==null||bankMap.size()<=0){
            UserBankCardInfo ubci = null;
            //缓存中没有用户对象信息 查询数据库
            ubci = userBankCardInfoService.findUserBankCardInfo(pursePayPassword.getUserId());
            if(ubci==null){
                return returnData(StatusCode.CODE_BANKCARD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未绑定过银行卡，无法设置支付密码!",new JSONObject());
            }
        }else{
            if(Integer.parseInt(bankMap.get("redisStatus").toString())==0) {//redisStatus==0 说明数据中无此记录
                return returnData(StatusCode.CODE_BANKCARD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未绑定过银行卡，无法设置支付密码!",new JSONObject());
            }
        }
        //验证之前是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pursePayPassword.getUserId() );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword ppp = null;
            //缓存中没有用户对象信息 查询数据库
            ppp = pursePayPasswordService.findPursePayPassword(pursePayPassword.getUserId());
            if(ppp!=null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_EXIST_ERROR.CODE_VALUE,"您已设置过支付密码，不能重复设置!",new JSONObject());
            }
        }else{
            if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==1) {//redisStatus==1 说明数据中已有此记录
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_EXIST_ERROR.CODE_VALUE,"您已设置过支付密码，不能重复设置!",new JSONObject());
            }
        }
        //开始设置密码
        String payCode = CommonUtils.getRandom(6,0);//生成随机数值
        String newPassWord = CommonUtils.getPasswordBySalt(pursePayPassword.getPayPassword(), payCode);//生成加盐的新密码
        pursePayPassword.setPayPassword(newPassWord);
        pursePayPassword.setPayCode(payCode);
        pursePayPasswordService.addPursePayPassword(pursePayPassword);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pursePayPassword.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 修改支付密码
     * @param pursePayPassword
     * @return
     */
    @Override
    public ReturnData updatePursePayPassword(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=pursePayPassword.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+pursePayPassword.getUserId()+"]的支付密码信息",new JSONObject());
        }
        //添加防暴力验证
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的支付密码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服",new JSONObject());
        }
        //验证之前是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pursePayPassword.getUserId() );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword ppp = null;
            //缓存中没有用户对象信息 查询数据库
            ppp = pursePayPasswordService.findPursePayPassword(pursePayPassword.getUserId());
            if(ppp==null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法修改支付密码!",new JSONObject());
            }
            payPasswordMap = CommonUtils.objectToMap(ppp);
        }else{
            if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==0) {//redisStatus==0说明数据中无此记录
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法修改支付密码!",new JSONObject());
            }
        }
        //验证旧密码是否正确
        String oldPassWord = CommonUtils.getPasswordBySalt(pursePayPassword.getPayPassword(), payPasswordMap.get("payCode").toString());//生成加盐的新密码
        if(!oldPassWord.equals(payPasswordMap.get("payPassword").toString())){
            //密码有误 添加错误限制 防暴力破解
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,pursePayPassword.getUserId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,pursePayPassword.getUserId()+"",1);
            }
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR.CODE_VALUE,"您输入的支付密码有误",new JSONObject());
        }
        //开始修改新密码
        String payCode = CommonUtils.getRandom(6,0);//生成随机数值
        String newPassWord = CommonUtils.getPasswordBySalt(pursePayPassword.getNewPayPassword(), payCode);//生成加盐的新密码
        pursePayPassword.setPayPassword(newPassWord);
        pursePayPassword.setPayCode(payCode);
        pursePayPasswordService.updatePursePayPassword(pursePayPassword);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pursePayPassword.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }


    /***
     * 验证旧支付密码是否正确（用于修改密码之前）
     * @param oldPayPassword 旧支付密码
     * @return
     */
    @Override
    public ReturnData checkPayPassword(@PathVariable String oldPayPassword) {
        //验证参数格式
        if(oldPayPassword.length()!=32){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"oldPayPassword参数有误",new JSONObject());
        }
        //添加防暴力验证
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的支付密码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服",new JSONObject());
        }
        //验证之前是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+CommonUtils.getMyId());
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword ppp = null;
            //缓存中没有用户对象信息 查询数据库
            ppp = pursePayPasswordService.findPursePayPassword(CommonUtils.getMyId());
            if(ppp==null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法验证支付密码的正确性!",new JSONObject());
            }
            payPasswordMap = CommonUtils.objectToMap(ppp);
        }else{
            if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==0) {//redisStatus==0说明数据中无此记录
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法验证支付密码的正确性!",new JSONObject());
            }
        }
        //验证旧密码是否正确
        String oldPassWord = CommonUtils.getPasswordBySalt(oldPayPassword, payPasswordMap.get("payCode").toString());//生成加盐的新密码
        if(!oldPassWord.equals(payPasswordMap.get("payPassword").toString())){
            //密码有误 添加错误限制 防暴力破解
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1);
            }
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR.CODE_VALUE,"您输入的支付密码有误",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 重置支付密码
     * @param pursePayPassword
     * @return
     */
    @Override
    public ReturnData resetPayPwd(@Valid @RequestBody PursePayPassword pursePayPassword, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        if(CommonUtils.checkFull(pursePayPassword.getPaymentKey())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"paymentKey参数有误",new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=pursePayPassword.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限重置用户["+pursePayPassword.getUserId()+"]的支付密码信息",new JSONObject());
        }
        //添加防暴力验证
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的支付密码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服",new JSONObject());
        }
        //验证之前是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pursePayPassword.getUserId() );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword ppp = null;
            //缓存中没有用户对象信息 查询数据库
            ppp = pursePayPasswordService.findPursePayPassword(pursePayPassword.getUserId());
            if(ppp==null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法重置支付密码!",new JSONObject());
            }
        }else{
            if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==0) {//redisStatus==0说明数据中无此记录
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法重置支付密码!",new JSONObject());
            }
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
        if(!pursePayPassword.getPaymentKey().equals(serverKey.toString())){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+"",1);
            }
            //清除秘钥
            redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYKEY+CommonUtils.getMyId(),0);
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE,"秘钥不正确，请重新验证银行卡信息找回密码!",new JSONObject());
        }
        //验证手机验证码是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_PAY_FIND_PAYPASSWORD_CODE+pursePayPassword.getUserId());
        if(serverCode==null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码已过期,请重新获取",new JSONObject());
        }
        if(!serverCode.toString().equals(pursePayPassword.getCode())){//不相等
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您输入的验证码有误,请重新输入",new JSONObject());
        }
        //开始修改新密码
        String payCode = CommonUtils.getRandom(6,0);//生成随机数值
        String newPassWord = CommonUtils.getPasswordBySalt(pursePayPassword.getPayPassword(), payCode);//生成加盐的新密码
        pursePayPassword.setPayPassword(newPassWord);
        pursePayPassword.setPayCode(payCode);
        pursePayPasswordService.updatePursePayPassword(pursePayPassword);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pursePayPassword.getUserId(),0);
        //清除秘钥
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYKEY+CommonUtils.getMyId(),0);
        //清除短信验证码
        redisUtils.expire(Constants.REDIS_KEY_PAY_FIND_PAYPASSWORD_CODE+pursePayPassword.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
