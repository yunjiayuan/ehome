package com.busi.controller.api;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CashOutService;
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

    @Autowired
    CashOutService cashOutService;

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
//            if(CommonUtils.checkFull(userInfo.getPhone())){
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您还未关联支付宝账号，请先绑定与支付宝账号相同的手机号，再进行提现操作",new JSONObject());
//            }
//            cashOut.setOpenid(userInfo.getPhone());
            if(CommonUtils.checkFull(cashOut.getOpenid())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"支付宝用户ID不能为空",new JSONObject());
            }
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

    /***
     * 查询提现记录列表
     * @param findType -1查询全部 2未到账 1已到账
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findRedPacketsList(@PathVariable int findType,@PathVariable int page, @PathVariable int count) {
        //验证参数
        if(findType<-1||findType>1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"findType参数有误",new JSONObject());
        }
        if(page<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        //验证身份
        long myId = CommonUtils.getMyId();
        if(myId!=10076&&myId!=12761&&myId!=12770&&myId!=9389&&myId!=9999&&myId!=13005&&myId!=12774&&myId!=13031&&myId!=12769&&myId!=12796&&myId!=10053){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始查询
        PageBean<CashOutOrder> pageBean;
        pageBean = cashOutService.findCashOutList(findType,page,count);
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        List list = pageBean.getList();
        if(list!=null&&list.size()>0){
            for (int i=0;i<list.size();i++){
                CashOutOrder cashOutOrder = (CashOutOrder) list.get(i);
                if(cashOutOrder!=null){
                    UserInfo userInfo = userInfoUtils.getUserInfo(cashOutOrder.getUserId());
                    if(userInfo!=null){
                        cashOutOrder.setUserName(userInfo.getName());
                        cashOutOrder.setUserHead(userInfo.getHead());
                        cashOutOrder.setHouseNumber(userInfo.getHouseNumber());
                        cashOutOrder.setProId(userInfo.getProType());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

    /***
     * 获取支付宝登录签名
     * @return
     */
    @Override
    public ReturnData getAliLoginSign() {
        Map<String,String> map = new HashMap();
        map.put("sign",AlipayUtils.getLoginSign());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
