package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.CashOutOrder;
import com.busi.entity.Purse;
import com.busi.entity.ReturnData;
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

    /***
     *  提现同步到微信或者支付宝
     * @param cashOutOrder
     * @return
     */
    @Override
    public ReturnData cashOutToOther(@RequestBody CashOutOrder cashOutOrder) {
        if(cashOutOrder.getType()==0){//提现到微信
            TransfersDto model = new TransfersDto();
            model.setMch_appid(Constants.WEIXIN_MCH_APPID);
            model.setMchid(Constants.WEIXIN_MCHID);
            model.setMch_name(Constants.WEIXIN_MCH_NAME);
            model.setOpenid(cashOutOrder.getOpenid());
            model.setAmount(cashOutOrder.getMoney());
            model.setDesc("提现");
            WechatpayUtil.doTransfers(model);
        }else if(cashOutOrder.getType()==1){//提现到支付宝

        }else {//提现到银行卡

        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
