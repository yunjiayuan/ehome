package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.PurseInfoService;
import com.busi.service.PursePayPasswordService;
import com.busi.service.RedPacketsInfoService;
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
 *  聊天系统 红包相关接口
 * author：SunTianJie
 * create time：2018/9/7 12:39
 */
@RestController
public class RedPacketsInfoController extends BaseController implements RedPacketsInfoApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RedPacketsInfoService redPacketsInfoService;

    @Autowired
    private PurseInfoService purseInfoService;

    @Autowired
    private PursePayPasswordService pursePayPasswordService;

    @Autowired
    private UserInfoUtils userInfoUtils;


    /***
     * 发送红包接口
     * @param redPacketsInfo
     * @return
     */
    @Override
    public ReturnData addRedPacketsInfo(@Valid @RequestBody RedPacketsInfo redPacketsInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //检测账户信息
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+redPacketsInfo.getSendUserId() );
        if(purseMap==null||purseMap.size()<=0){
            Purse purse = null;
            //缓存中没有用户对象信息 查询数据库
            purse = purseInfoService.findPurseInfo(redPacketsInfo.getSendUserId());
            if(purse==null){
                return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行兑换操作",new JSONObject());
            }
            purseMap = CommonUtils.objectToMap(purse);
        }
        //检测是否设置过支付密码
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+redPacketsInfo.getSendUserId() );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword pursePayPassword = null;
            //缓存中没有用户对象信息 查询数据库
            pursePayPassword = pursePayPasswordService.findPursePayPassword(redPacketsInfo.getSendUserId());
            if(pursePayPassword==null){
                return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您当前账户尚未设置过支付密码，无法进行兑换操作",new JSONObject());
            }
        }
        //判断余额
        double spareMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if(spareMoney<redPacketsInfo.getRedPacketsMoney()){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法发红包操作",new JSONObject());
        }
        //生成红包订单
        redPacketsInfo.setPayStatus(0);//未支付
        redPacketsInfo.setDelStatus(0);//删除状态 正常
        redPacketsInfo.setRedPacketsStatus(0);//红包状态 已发送
        redPacketsInfo.setSendTime(new Date());
        redPacketsInfoService.addRedPacketsInfo(redPacketsInfo);
        //将订单放入缓存中  5分钟有效时间  超时作废
        String orderNumber = CommonUtils.strToMD5(redPacketsInfo.getId()+"",16);
        redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_REDPACKETSINFO+orderNumber,CommonUtils.objectToMap(redPacketsInfo),Constants.TIME_OUT_MINUTE_5);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("orderNumber",orderNumber);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 根据红包ID查询红包信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findRedPacketsInfo(@PathVariable long id) {
        //验证参数
        if(id<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"id参数有误",new JSONObject());
        }
        RedPacketsInfo redPacketsInfo = redPacketsInfoService.findRedPacketsInfo(CommonUtils.getMyId(),id);
        if(redPacketsInfo!=null){
            int status = redPacketsInfo.getRedPacketsStatus();//0正常 1过期 2已拆（已接收）
            if(status==0&&redPacketsInfo.getReceiveTime()==null){//处理定时任务未来得及处理的红包过期数据
                //判断该红包是否已过期 24小时
                long nowTime = new Date().getTime();
                long sendTime = redPacketsInfo.getSendTime().getTime();
                int countTime = 24*60*60*1000;
                if(nowTime-sendTime>countTime){//已过期
                    status = 1;
                }
            }
            redPacketsInfo.setRedPacketsStatus(status);
            UserInfo sendUserInfo = userInfoUtils.getUserInfo(redPacketsInfo.getSendUserId());
            if(sendUserInfo!=null){
                redPacketsInfo.setSendUserName(sendUserInfo.getName());
                redPacketsInfo.setSendUserHead(sendUserInfo.getHead());
            }
            UserInfo receiveUserInfo = userInfoUtils.getUserInfo(redPacketsInfo.getReceiveUserId());
            if(receiveUserInfo!=null){
                redPacketsInfo.setReceiveUserName(receiveUserInfo.getName());
                redPacketsInfo.setReceiveUserHead(receiveUserInfo.getHead());
            }
        }else{
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",redPacketsInfo);
    }
}
