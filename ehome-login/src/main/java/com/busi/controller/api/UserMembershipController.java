package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.MemberOrder;
import com.busi.entity.ReturnData;
import com.busi.entity.UserMembership;
import com.busi.service.UserMembershipService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员相关接口
 * author：SunTianJie
 * create time：2018/8/9 14:34
 */
@RestController
public class UserMembershipController extends BaseController implements UserMembershipApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserMembershipService userMembershipService;

    /***
     * 查询用户会员信息
     * @param userId 被查询者的用户ID
     * @return
     */
    @Override
    public ReturnData findUserMembershipInfo(@PathVariable long userId) {
        //验证修改人权限
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userId+"]的会员信息",new JSONObject());
        }
        Map<String,Object> userMembershipMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+userId );
        if(userMembershipMap==null||userMembershipMap.size()<=0){
            UserMembership userMembership = null;
            //缓存中没有用户对象信息 查询数据库
            userMembership = userMembershipService.findUserMembership(userId);
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(userId);
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            userMembershipMap = CommonUtils.objectToMap(userMembership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP+userId,userMembershipMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userMembershipMap);
    }

    /***
     * 购买会员下单接口
     * @param memberOrder
     * @return
     */
    @Override
    public ReturnData addMemberOrder(@Valid @RequestBody MemberOrder memberOrder, BindingResult bindingResult) {
        //验证参数
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证下单用户权限
        if(CommonUtils.getMyId()!=memberOrder.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误，自己不能给其他人购买会员",new JSONObject());
        }
        //获取当前用户的会员信息
        Map<String,Object> userMembershipMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+memberOrder.getUserId() );
        if(userMembershipMap==null||userMembershipMap.size()<=0){
            UserMembership userMembership = null;
            //缓存中没有用户对象信息 查询数据库
            userMembership = userMembershipService.findUserMembership(memberOrder.getUserId());
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(memberOrder.getUserId());
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            userMembershipMap = CommonUtils.objectToMap(userMembership);
        }
        //当前会员状态 1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
        int memberShipStatus = Integer.parseInt(userMembershipMap.get("memberShipStatus").toString());
        //判断购买会员的类型
        switch (memberOrder.getExpireType()) {//将要购买的会员类型

            case 0://0表示购买创始元老级会员
                int initiatorMembershipMoney[] = {100,200,300,400,500}; //创始元老级会员套餐数组
                memberOrder.setMoney(initiatorMembershipMoney[memberOrder.getNumber()]);
                break;
            case 1://1购买元老级会员
                memberOrder.setMoney(100);//元老级会员 固定100元
                break;
            case 2://2表示购买普通会员
                int regularExpireMoney[] = {12,30,60,108}; //普通会员套餐数组
                memberOrder.setMoney(regularExpireMoney[memberOrder.getNumber()]);
                if(memberOrder.getNumber()==0){
                    memberOrder.setMonthNumber(1);
                }else{
                    memberOrder.setMonthNumber(memberOrder.getNumber()*3);//1 3 6 9
                }
                break;
            case 3://3表示购买高级会员
                int vipExpireMoney[] = {30,88,168,298}; //VIP高级会员套餐数组
                if(memberOrder.getPayType()==0){//购买
                    memberOrder.setMoney(vipExpireMoney[memberOrder.getNumber()]);
                    if(memberOrder.getNumber()==0){
                        memberOrder.setMonthNumber(1);
                    }else{
                        memberOrder.setMonthNumber(memberOrder.getNumber()*3);//1 3 6 9
                    }
                }else{//升级
                    int count = 24*60*60*1000;
                    long days = 0L;
                    Date regularExpireTime = null;
                    Date regularStopTime = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        regularExpireTime = sdf.parse(userMembershipMap.get("regularExpireTime").toString());
                        regularStopTime = sdf.parse(userMembershipMap.get("regularStopTime").toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(memberShipStatus>1){//比普通会员高级时  普通会员之前已暂停过
                        days = (regularExpireTime.getTime()-regularStopTime.getTime())/count;
                    }else{
                        days = (regularExpireTime.getTime()-new Date().getTime())/count;
                    }
                    long lastMonths = days/31;//普通会员剩余总整月数
//					long surplusDays = days%31;//多余天数
                    double vipMoney = vipExpireMoney[memberOrder.getNumber()];
                    int months = (int) (vipMoney/12);//每个月按12元 计算购买的套餐最多能用多少个月的普通会员抵扣
                    double money = 0 ;//用户需要支付的钱数
                    if(lastMonths<=months){
                        money = vipMoney - lastMonths*12;//用户将要支付的钱数
                        memberOrder.setDeductMonthNumber((int)lastMonths);//抵扣的普通会员月数
                    }else{
                        money = vipMoney - months*12;//用户将要支付的钱数
                        memberOrder.setDeductMonthNumber(months);//抵扣的普通会员月数
                    }
                    memberOrder.setMoney(money);
                    if(memberOrder.getNumber()==0){
                        memberOrder.setMonthNumber(1);
                    }else{
                        memberOrder.setMonthNumber(memberOrder.getNumber()*3);//1 3 6 9
                    }
                }
                break;
            default:
                break;
        }
        //生成订单号
        memberOrder.setOrderNumber(CommonUtils.getOrderNumber(memberOrder.getUserId(),Constants.REDIS_KEY_PAY_ORDER_EXCHANGE));
        memberOrder.setPayState(0);//未支付
        memberOrder.setTime(new Date());
        //将订单放入缓存中  5分钟有效时间  超时作废
        redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_EXCHANGE+memberOrder.getUserId()+"_"+memberOrder.getOrderNumber(),CommonUtils.objectToMap(memberOrder),Constants.TIME_OUT_MINUTE_5);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("orderNumber",memberOrder.getOrderNumber());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
