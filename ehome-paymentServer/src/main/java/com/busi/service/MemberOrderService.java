package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Pay;
import com.busi.entity.ReturnData;
import com.busi.entity.UserMembership;
import com.busi.fegin.UserMemberControllerFegin;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;

/**
 * 支付--购买会员业务
 * author：SunTianJie
 * create time：2018/8/28 15:21
 */
@Service
public class MemberOrderService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private UserMemberControllerFegin userMemberControllerFegin;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay,Map<String,Object> purseMap) {

        //获取未支付的订单
        Map<String,Object> memberOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_MEMBER+pay.getUserId()+"_"+pay.getOrderNumber() );
        if(memberOrderMap==null||memberOrderMap.size()<=0||Integer.parseInt(memberOrderMap.get("payState").toString())!=0){
            return returnData(StatusCode.CODE_PAY_OBJECT_NOT_EXIST_ERROR.CODE_VALUE,"由于您等待时间过久或网络延迟导致购买会员失败，请重新购买",new JSONObject());
        }
        //判断余额
        double money = Double.parseDouble(memberOrderMap.get("money").toString());//将要花费的钱
        int monthNumber = Integer.parseInt(memberOrderMap.get("monthNumber").toString());//购买的月份
        int deductMonthNumber = Integer.parseInt(memberOrderMap.get("deductMonthNumber").toString());//抵扣的普通会员的月数
        int payType = Integer.parseInt(memberOrderMap.get("payType").toString());//购买方式  0直接购买  1升级购买  此参数只在expireType=3时有效
        double serverMoney = Double.parseDouble(purseMap.get("spareMoney").toString());
        if(serverMoney<money){
            return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行购买会员操作",new JSONObject());
        }
        //更改状态 防止重复支付
        redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_MEMBER+pay.getUserId()+"_"+pay.getOrderNumber(),"payState",1);
        //获取当前用户的会员状态
        Map<String,Object> userMembershipMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+pay.getUserId());
        if(userMembershipMap==null||userMembershipMap.size()<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"您当前的会员状态存在异常，建议重新登录后，再尝试购买",new JSONObject());
        }
        UserMembership userMembership = (UserMembership) CommonUtils.mapToObject(userMembershipMap,UserMembership.class);
        if(userMembership==null){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"您当前的会员状态存在异常，建议重新登录后，再尝试购买",new JSONObject());
        }
        //当前会员状态 1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
        int memberShipStatus = userMembership.getMemberShipStatus();
        //判断购买会员类型
        switch (pay.getServiceType()) {//将要购买的会员类型

            case 6://购买创始元老级会员支付
                //创始元老级会员状态
                int initiatorMembershipLevel = userMembership.getInitiatorMembershipLevel();
                if(initiatorMembershipLevel>0){
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"您已为创始元老级会员，无需再次购买!",new JSONObject());
                }
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),17,0,money*-1);
                //回调业务 更新会员状态
                int leve = (int)money/100;//创始元老级会员等级
                if(userMembership.getRedisStatus()==0) {//redisStatus==0 说明数据库中无此记录
                    //新增
                    UserMembership ums = new UserMembership();
                    ums.setInitiatorMembershipLevel(leve);
                    ums.setMemberShipStatus(4);
                    ums.setInitiatorMembershipTime(new Date());//创始元老级会员开通时间
                    ums.setUserId(pay.getUserId());
                    userMemberControllerFegin.addUserMember(ums);
                }else{
                    //更新
                    userMembership.setInitiatorMembershipLevel(leve);
                    userMembership.setInitiatorMembershipTime(new Date());//创始元老级会员开通时间
                    userMembership.setMemberShipStatus(4);
                    if(memberShipStatus==1){//之前为普通会员
                        userMembership.setRegularStopTime(new Date());//将普通会员停用
                    }else if(memberShipStatus==2){//之前为高级会员
                        userMembership.setVipStopTime(new Date());//将VIP高级会员停用
                    }
                    userMemberControllerFegin.updateUserMember(userMembership);
                }
                break;
            case  9://购买元老级会员支付
                //元老级会员状态
                int membershipLevel = userMembership.getMembershipLevel();
                if(membershipLevel>0){
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"您已为元老级会员，无需再次购买!",new JSONObject());
                }
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),17,0,money*-1);
                //回调业务 更新会员状态
                if(userMembership.getRedisStatus()==0) {//redisStatus==0 说明数据库中无此记录
                    //新增
                    UserMembership ums = new UserMembership();
                    ums.setMembershipLevel(1);//暂定为1一级元老级会员
                    ums.setMemberShipStatus(3);
                    ums.setMembershipTime(new Date());//元老级会员开通时间
                    ums.setUserId(pay.getUserId());
                    userMemberControllerFegin.addUserMember(ums);
                }else{
                    //更新
                    userMembership.setMembershipLevel(1);//暂定为1一级元老级会员
                    userMembership.setMembershipTime(new Date());//元老级会员开通时间
                    userMembership.setMemberShipStatus(3);
                    if(memberShipStatus==1){//之前为普通会员
                        userMembership.setRegularStopTime(new Date());//将普通会员停用
                    }else if(memberShipStatus==2){//之前为高级会员
                        userMembership.setVipStopTime(new Date());//将VIP高级会员停用
                    }
                    userMemberControllerFegin.updateUserMember(userMembership);
                }
                break;
            case 10://购买普通会员支付
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),17,0,money*-1);
                //回调业务 更新会员状态
                if(userMembership.getRedisStatus()==0) {//redisStatus==0 说明数据库中无此记录
                    //新增
                    UserMembership ums = new UserMembership();
                    ums.setRegularMembershipLevel(1);//暂定为1一级普通会员
                    ums.setMemberShipStatus(1);//普通会员
                    //计算普通会员到期时间
                    Date date = new Date(new Date().getTime()+monthNumber*31*24*60*60*1000L);
                    ums.setRegularExpireTime(date);//普通会员到期时间
                    ums.setUserId(pay.getUserId());
                    userMemberControllerFegin.addUserMember(ums);
                }else{//更新
                    if(userMembership.getMemberShipStatus()==0){//当前处于未开通会员状态
                        Date date = new Date(new Date().getTime()+monthNumber*31*24*60*60*1000L);
                        userMembership.setRegularExpireTime(date);//普通会员到期时间
                        userMembership.setRegularMembershipLevel(1);//暂定为1一级普通会员
                        userMembership.setMemberShipStatus(1);
                    }else if(userMembership.getMemberShipStatus()==1){//当前是普通会员 	在原先到期时间基础上添加
                        Date date = new Date(userMembership.getRegularExpireTime().getTime()+monthNumber*31*24*60*60*1000L);
                        userMembership.setRegularExpireTime(date);//普通会员到期时间
                    }else {//当前处于更高级会员状态 在原先到期时间基础上添加
                        //判断之前是否开通过普通会员 并处于暂停状态
                        if(userMembership.getRegularMembershipLevel()>0){//开通过
                            Date date = new Date(userMembership.getRegularExpireTime().getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setRegularExpireTime(date);//普通会员到期时间
                        }else{//之前未开通过  第一次开通
                            Date todayTime = new Date();
                            Date date = new Date(todayTime.getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setRegularExpireTime(date);//普通会员到期时间
                            userMembership.setRegularStopTime(todayTime);//设置暂停时间
                            userMembership.setRegularMembershipLevel(1);
                        }
                    }
                    userMemberControllerFegin.updateUserMember(userMembership);
                }
                break;
            case 11://购买VIP高级会员支付
                //开始扣款支付
                mqUtils.sendPurseMQ(pay.getUserId(),17,0,money*-1);
                //回调业务 更新会员状态
                if(userMembership.getRedisStatus()==0) {//redisStatus==0 说明数据库中无此记录
                    //新增
                    UserMembership ums = new UserMembership();
                    ums.setVipMembershipLevel(1);//暂定为1一级VIP高级会员
                    ums.setMemberShipStatus(2);//VIP高级会员
                    //计算VIP高级会员到期时间
                    Date date = new Date(new Date().getTime()+monthNumber*31*24*60*60*1000);
                    ums.setVipExpireTime(date);//VIP高级会员到期时间
                    ums.setUserId(pay.getUserId());
                    userMemberControllerFegin.addUserMember(ums);
                }else{//更新
                    Date todayTime = new Date();
                    //判断是直接购买还是升级
                    if(payType==0){//直接购买
                        if(userMembership.getMemberShipStatus()==0){//当前处于未开通会员状态
                            Date date = new Date(todayTime.getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setVipExpireTime(date);//VIP高级会员到期时间
                            userMembership.setVipMembershipLevel(1);
                            userMembership.setMemberShipStatus(2);
                        }else if(userMembership.getMemberShipStatus()==1){//当前处于普通会员状态
                            Date date = new Date(todayTime.getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setVipExpireTime(date);//VIP高级会员到期时间
                            userMembership.setVipMembershipLevel(1);
                            userMembership.setMemberShipStatus(2);
                            //将普通会员停用
                            userMembership.setRegularStopTime(todayTime);
                        }else if(userMembership.getMemberShipStatus()==2){//当前处于VIP高级会员状态
                            Date date = new Date(userMembership.getVipExpireTime().getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setVipExpireTime(date);//VIP高级会员到期时间
                        }else {//当前处于其他会员状态 在原先到期时间基础上添加
                            //判断之前是否开通过高级会员 并已处于暂定状态
                            if(userMembership.getVipMembershipLevel()>0){//开通过
                                Date date = new Date(userMembership.getVipExpireTime().getTime()+monthNumber*31*24*60*60*1000L);
                                userMembership.setVipExpireTime(date);//VIP高级会员到期时间
                            }else{//未开通过 第一次开通
                                Date date = new Date(todayTime.getTime()+monthNumber*31*24*60*60*1000L);
                                userMembership.setVipExpireTime(date);//VIP高级会员到期时间
                                userMembership.setVipMembershipLevel(1);
                                userMembership.setVipStopTime(todayTime);//设置VIP高级会员停用时间
                            }
                        }
                    }else{//升级
                        if(userMembership.getMemberShipStatus()==0){//当前处于未开通会员状态
                            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"您当前并未开通任何会员，无法升级为VIP高级会员，请直接购买!",new JSONObject());
                        }else if(userMembership.getMemberShipStatus()==1){//当前处于普通会员状态
                            //更新普通会员到期时间
                            Date regularExpireNewDate = new Date(userMembership.getRegularExpireTime().getTime()-deductMonthNumber*31*24*60*60*1000L);
                            userMembership.setRegularExpireTime(regularExpireNewDate);
                            userMembership.setRegularStopTime(todayTime);
                            //更新高级会员到期时间 第一次开通高级会员
                            Date vipExpireNewDate = new Date(todayTime.getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setVipExpireTime(vipExpireNewDate);
                            userMembership.setVipMembershipLevel(1);//暂定为1一级VIP高级会员
                            userMembership.setMemberShipStatus(2);//设为高级会员
                        }else if(userMembership.getMemberShipStatus()==2){//当前处于VIP高级会员状态
                            //更新普通会员到期时间
                            Date regularExpireNewDate = new Date(userMembership.getRegularExpireTime().getTime()-deductMonthNumber*31*24*60*60*1000L);
                            userMembership.setRegularExpireTime(regularExpireNewDate);
//								uMembership4.setRegularStopTime(todayTime);//当前处于高级会员状态 普通会员之前已经暂停
                            //更新高级会员到期时间 在原先到期时间基础上添加
                            Date vipExpireNewDate = new Date(userMembership.getVipExpireTime().getTime()+monthNumber*31*24*60*60*1000L);
                            userMembership.setVipExpireTime(vipExpireNewDate);
                        }else {//当前处于其他会员状态 在原先到期时间基础上添加
                            //判断之前是否开通过高级会员 并已处于暂定状态
                            if(userMembership.getVipMembershipLevel()>0){//开通过
                                //更新普通会员到期时间
                                Date regularExpireNewDate = new Date(userMembership.getRegularExpireTime().getTime()-deductMonthNumber*31*24*60*60*1000L);
                                userMembership.setRegularExpireTime(regularExpireNewDate);
                                //更新高级会员到期时间 在原先到期时间基础上添加
                                Date vipExpireNewDate = new Date(userMembership.getVipExpireTime().getTime()+monthNumber*31*24*60*60*1000L);
                                userMembership.setVipExpireTime(vipExpireNewDate);
                            }else{//未开通过 第一次开通
                                //更新普通会员到期时间
                                Date regularExpireNewDate = new Date(userMembership.getRegularExpireTime().getTime()-deductMonthNumber*31*24*60*60*1000L);
                                userMembership.setRegularExpireTime(regularExpireNewDate);
                                //更新高级会员到期时间 在原先到期时间基础上添加
                                Date vipExpireNewDate = new Date(todayTime.getTime()+monthNumber*31*24*60*60*1000L);
                                userMembership.setVipExpireTime(vipExpireNewDate);
                                userMembership.setVipStopTime(todayTime);//设置VIP高级会员停用时间
                                userMembership.setVipMembershipLevel(1);
                            }
                        }
                    }
                    userMemberControllerFegin.updateUserMember(userMembership);
                }
                break;
            default:
                break;
        }
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_USERMEMBERSHIP+pay.getUserId(),0);//设置过期0秒
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
