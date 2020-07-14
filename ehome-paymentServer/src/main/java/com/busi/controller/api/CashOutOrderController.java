package com.busi.controller.api;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  提现接口
 * author：SunTianJie
 * create time：2020-7-1 14:46:44
 */
@RestController
public class CashOutOrderController extends BaseController implements CashOutOrderApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    /***
     * 提现下单接口
     * @param cashOut
     * @return
     */
    @Override
    public ReturnData cashOut(@Valid @RequestBody CashOutOrder cashOut, BindingResult bindingResult) {
        //验证参数
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=cashOut.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限提现用户["+cashOut.getUserId()+"]的钱包金额",new JSONObject());
        }
        //生成订单
        String orderNumber = CommonUtils.getOrderNumber(cashOut.getUserId(),Constants.REDIS_KEY_PAY_ORDER_CASHOUT);
        cashOut.setId(orderNumber);
        cashOut.setPayStatus(0);//未支付
        cashOut.setTime(new Date());
        UserInfo userInfo = userInfoUtils.getUserInfo(cashOut.getUserId());
        if(userInfo==null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"当前用户账号异常，请重新登录后再尝试提现操作",new JSONObject());
        }
        if(cashOut.getType()==0){//提现到微信
            if(CommonUtils.checkFull(userInfo.getOtherPlatformKey())||userInfo.getOtherPlatformType()!=2){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"当前用户还未绑定微信，无法提现到微信",new JSONObject());
            }
            cashOut.setOpenid(userInfo.getOtherPlatformKey());
            //将订单放入缓存中  15分钟有效时间  超时作废
            redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+orderNumber,CommonUtils.objectToMap(cashOut),Constants.TIME_OUT_MINUTE_15);
            //响应客户端
            Map<String,String> map = new HashMap();
            map.put("orderNumber",orderNumber);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
        }else if(cashOut.getType()==1){//提现到支付宝
            if(CommonUtils.checkFull(userInfo.getPhone())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您还未关联支付宝账号，请先绑定与支付宝账号相同的手机号，再进行提现操作",new JSONObject());
            }
            cashOut.setOpenid(userInfo.getPhone());
            //将订单放入缓存中  15分钟有效时间  超时作废
            redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+orderNumber,CommonUtils.objectToMap(cashOut),Constants.TIME_OUT_MINUTE_15);
            //响应客户端
            Map<String,String> map = new HashMap();
            map.put("orderNumber",orderNumber);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
        }else{//预留
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }

    }
}
