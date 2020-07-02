package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.PurseInfoService;
import com.busi.service.PursePayPasswordService;
import com.busi.service.TransferAccountsInfoService;
import com.busi.utils.*;
import com.google.gson.JsonObject;
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
 *  转账相关接口
 * author：SunTianJie
 * create time：2020-7-1 14:46:44
 */
@RestController
public class TransferAccountsInfoController extends BaseController implements TransferAccountsInfoApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private TransferAccountsInfoService transferAccountsInfoService;

    @Autowired
    private PurseInfoService purseInfoService;

    @Autowired
    private PursePayPasswordService pursePayPasswordService;

    @Autowired
    private UserInfoUtils userInfoUtils;


    /***
     * 发送转账接口
     * @param transferAccountsInfo
     * @return
     */
    @Override
    public ReturnData addTransferAccountsInfo(@Valid @RequestBody TransferAccountsInfo transferAccountsInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //检测账户信息
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+transferAccountsInfo.getSendUserId() );
        if(purseMap==null||purseMap.size()<=0){
            Purse purse = null;
            //缓存中没有用户对象信息 查询数据库
            purse = purseInfoService.findPurseInfo(transferAccountsInfo.getSendUserId());
            if(purse==null){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行转账操作",new JSONObject());
            }
            purseMap = CommonUtils.objectToMap(purse);
        }
        //检测是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+transferAccountsInfo.getSendUserId() );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword pursePayPassword = null;
            //缓存中没有用户对象信息 查询数据库
            pursePayPassword = pursePayPasswordService.findPursePayPassword(transferAccountsInfo.getSendUserId());
            if(pursePayPassword==null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您当前账户尚未设置过支付密码，无法进行转账操作",new JSONObject());
            }
        }
        //判断余额
        double spareMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if(spareMoney<transferAccountsInfo.getTransferAccountsMoney()){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行转账操作",new JSONObject());
        }
        //生成红包订单
        String orderNumber = CommonUtils.getOrderNumber(transferAccountsInfo.getSendUserId(),Constants.REDIS_KEY_PAY_ORDER_TRANSFERACCOUNTSINFO);
        transferAccountsInfo.setId(orderNumber);
        transferAccountsInfo.setPayStatus(0);//未支付
        transferAccountsInfo.setDelStatus(0);//删除状态 正常
        transferAccountsInfo.setTransferAccountsStatus(0);//转账状态 已发送
        transferAccountsInfo.setSendTime(new Date());
        transferAccountsInfoService.addTransferAccountsInfo(transferAccountsInfo);
        //将订单放入缓存中  5分钟有效时间  超时作废
        redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_TRANSFERACCOUNTSINFO+orderNumber,CommonUtils.objectToMap(transferAccountsInfo),Constants.TIME_OUT_MINUTE_5);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("orderNumber",orderNumber);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 根据转账ID查询转账信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findTransferAccountsInfo(@PathVariable String id) {
        //验证参数
        if(id.length()!=16){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"id参数有误",new JSONObject());
        }
        TransferAccountsInfo transferAccountsInfo = transferAccountsInfoService.findTransferAccountsInfo(CommonUtils.getMyId(),id);
        if(transferAccountsInfo!=null){
            int status = transferAccountsInfo.getTransferAccountsStatus();//转账状态 0正常（已发送，未拆收） 1过期自动退回  2已接收  3接收者主动退回
            if(status==0&&transferAccountsInfo.getReceiveTime()==null){//处理定时任务未来得及处理的转账过期数据
                //判断该转账是否已过期 24小时
                long nowTime = new Date().getTime();
                long sendTime = transferAccountsInfo.getSendTime().getTime();
                int countTime = 24*60*60*1000;
                if(nowTime-sendTime>countTime){//已过期
                    status = 1;
                }
            }
            transferAccountsInfo.setTransferAccountsStatus(status);
            UserInfo sendUserInfo = userInfoUtils.getUserInfo(transferAccountsInfo.getSendUserId());
            if(sendUserInfo!=null){
                transferAccountsInfo.setSendUserName(sendUserInfo.getName());
                transferAccountsInfo.setSendUserHead(sendUserInfo.getHead());
            }
            UserInfo receiveUserInfo = userInfoUtils.getUserInfo(transferAccountsInfo.getReceiveUserId());
            if(receiveUserInfo!=null){
                transferAccountsInfo.setReceiveUserName(receiveUserInfo.getName());
                transferAccountsInfo.setReceiveUserHead(receiveUserInfo.getHead());
            }
        }else{
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",transferAccountsInfo);
    }

    /***
     * 转账退款
     * @param id
     * @return
     */
    @Override
    public ReturnData transferAccountsRefund(@PathVariable String id) {
        //验证参数
        if(id.length()!=16){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"id参数有误",new JSONObject());
        }
        TransferAccountsInfo transferAccountsInfo = transferAccountsInfoService.findTransferAccountsInfo(CommonUtils.getMyId(),id);
        if(transferAccountsInfo!=null){
            int status = transferAccountsInfo.getTransferAccountsStatus();//转账状态 0正常（已发送，未拆收） 1过期自动退回  2已接收  3接收者主动退回
            if(status==0&&transferAccountsInfo.getReceiveTime()==null){
                //判断该转账是否已过期 24小时
                long nowTime = new Date().getTime();
                long sendTime = transferAccountsInfo.getSendTime().getTime();
                int countTime = 24*60*60*1000;
                if(nowTime-sendTime>countTime){//已过期
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"很抱歉，该转账已过期，无法再进行退款操作",new JSONObject());
                }
                //开始退款 将转账金额放回账户
                mqUtils.sendPurseMQ(transferAccountsInfo.getSendUserId(),36,0,transferAccountsInfo.getTransferAccountsMoney());
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }else{//非正常状态下 无法退款
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"很抱歉，该转账已接收或已过期，无法再进行退款操作",new JSONObject());
            }
        }else{
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"很抱歉，没有找到要退款的转账记录",new JSONObject());
        }
    }

}
