package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
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
import java.util.List;
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
        String orderNumber = CommonUtils.getOrderNumber(redPacketsInfo.getSendUserId(),Constants.REDIS_KEY_PAY_ORDER_REDPACKETSINFO);
        redPacketsInfo.setId(orderNumber);
        redPacketsInfo.setPayStatus(0);//未支付
        redPacketsInfo.setDelStatus(0);//删除状态 正常
        redPacketsInfo.setRedPacketsStatus(0);//红包状态 已发送
        redPacketsInfo.setSendTime(new Date());
        redPacketsInfoService.addRedPacketsInfo(redPacketsInfo);
        //将订单放入缓存中  5分钟有效时间  超时作废
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
    public ReturnData findRedPacketsInfo(@PathVariable String id) {
        //验证参数
        if(id.length()!=16){
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

    /***
     * 接收(拆)红包后留言接口
     * @param redPacketsInfo
     * @return
     */
    @Override
    public ReturnData receiveMessage(@RequestBody RedPacketsInfo redPacketsInfo) {
        //验证参数格式
        if(redPacketsInfo.getId().length()!=16){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"id参数格式有误",new JSONObject());
        }
        if(CommonUtils.getStringLengsByByte(redPacketsInfo.getReceiveMessage())>50){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"receiveMessage参数有误,字数太多了",new JSONObject());
        }
        if(CommonUtils.getMyId()!=redPacketsInfo.getReceiveUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权给用户["+redPacketsInfo.getReceiveUserId()+"]的红包进行留言",new JSONObject());
        }
        RedPacketsInfo rpi = redPacketsInfoService.findRedPacketsInfo(redPacketsInfo.getReceiveUserId(),redPacketsInfo.getId());
        if(rpi==null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您将要留言的红包不存在",new JSONObject());
        }
        if(rpi.getReceiveUserId()!=redPacketsInfo.getReceiveUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您无权限对该红包进行留言",new JSONObject());
        }
        if(rpi.getRedPacketsStatus()!=2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您将要留言的红包尚未领取，请领取后再留言",new JSONObject());
        }
        if(!CommonUtils.checkFull(rpi.getReceiveMessage())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您已经留过言了，无法再进行留言",new JSONObject());
        }
        //开始留言
        redPacketsInfoService.updateRedPacketsReceiveMessage(redPacketsInfo);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 查询红包记录列表
     * @param userId   被查询用户ID
     * @param findType 1查询我发的红包 2查询我收的红包列表
     * @param time   	查询年份 格式2017  起始值2017
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findRedPacketsList(@PathVariable long userId,@PathVariable int findType,@PathVariable int time,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(findType<0||findType>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"findType参数有误",new JSONObject());
        }
        if(page<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,您无权限进行此操作",new JSONObject());
        }
        //开始查询
        PageBean<RedPacketsInfo> pageBean;
        pageBean = redPacketsInfoService.findRedPacketsInfoList(findType,userId,time,page,count);
        List list = pageBean.getList();
        if(list!=null&&list.size()>0){
            for (int i=0;i<list.size();i++){
                RedPacketsInfo redPacketsInfo = (RedPacketsInfo) list.get(i);
                if(redPacketsInfo!=null){
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
                }
            }
        }
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }
}
