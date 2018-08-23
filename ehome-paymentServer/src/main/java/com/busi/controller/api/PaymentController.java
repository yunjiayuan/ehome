package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
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
}
