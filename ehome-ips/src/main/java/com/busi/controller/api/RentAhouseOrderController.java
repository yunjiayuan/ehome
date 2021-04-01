package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.RentAhouseOrderService;
import com.busi.service.RentAhouseService;
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
 * @program: ehome
 * @description: 租房买房订单相关接口
 * @author: ZHaoJiaJie
 * @create: 2021-03-29 17:45:12
 */
@RestController
public class RentAhouseOrderController extends BaseController implements RentAhouseOrderApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    RentAhouseService communityService;

    @Autowired
    RentAhouseOrderService rentAhouseOrderService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增订单
     * @param order
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHouseOrders(@Valid @RequestBody RentAhouseOrder order, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        //判断是否是续租
        if (!CommonUtils.checkFull(order.getNo())) {
            //查询缓存 缓存中不存在 查询数据库
            RentAhouseOrder io = null;
            Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_RENTAHOUSE_ORDER + CommonUtils.getMyId() + "_" + order.getNo());
            if (ordersMap == null || ordersMap.size() <= 0) {
                io = rentAhouseOrderService.findNo(order.getNo());
                if (io == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
                }
                //放入缓存
                ordersMap = CommonUtils.objectToMap(io);
                redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + io.getMyId() + "_" + order.getNo(), ordersMap, Constants.USER_TIME_OUT);
            }
            RentAhouseOrder ahouseOrder = (RentAhouseOrder) CommonUtils.mapToObject(ordersMap, RentAhouseOrder.class);
            if (ahouseOrder == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            ahouseOrder.setRenewalState(1);
            ahouseOrder.setMakeMoneyStatus(0);
            int num = 0;
            int paymentMethod = ahouseOrder.getPaymentMethod();  //支付方式 0押一付一 1押一付三 2季付 3半年付 4年付
            ahouseOrder.setPaymentMethod(paymentMethod);
            if (paymentMethod == 0) {
                num = 1;
            }
            if (paymentMethod == 1 || paymentMethod == 2) {
                num = 3;
            }
            if (paymentMethod == 3) {
                num = 6;
            }
            if (paymentMethod == 4) {
                num = 12;
            }
            ahouseOrder.setPrice(num * ahouseOrder.getDeposit());//本次支付总金额
            ahouseOrder.setDuration(ahouseOrder.getDuration() + num); //已累计支付房租时长
            ahouseOrder.setRentMoney(num * ahouseOrder.getDeposit() + ahouseOrder.getRentMoney());//已累计支付房租金额
            rentAhouseOrderService.upOrders(ahouseOrder);
            map.put("infoId", ahouseOrder.getNo());
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE_ORDER + ahouseOrder.getMyId() + "_" + ahouseOrder.getNo(), 0);

            //放入缓存
            // 付款超时 45分钟
            redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + ahouseOrder.getMyId() + "_" + ahouseOrder.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_45);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
        }

        //*******第一次下单********
        RentAhouse sa = communityService.findRentAhouse(order.getHouseId());
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "房源不存在!", new JSONObject());
        }
        //判断协议是否签署（暂不启用）
//        if (order.getLeaseContract() == 0) {
//            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您还未签署协议", new JSONObject());
//        }
        //判断该用户是否实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(order.getMyId());
        if (userAccountSecurity != null) {
            if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "您还未实名认证", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "您还未实名认证", new JSONObject());
        }
        long time1 = new Date().getTime();
        String noTime = String.valueOf(time1);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + CommonUtils.getMyId() + random, 16);
        order.setNo(noRandom);
        order.setVillageName(sa.getVillageName());
        order.setPicture(sa.getPicture());
        order.setProvince(sa.getProvince());
        order.setCity(sa.getCity());
        order.setDistrict(sa.getDistrict());
        order.setHouseNumber(sa.getHouseNumber());
        order.setHouseCompany(sa.getHouseCompany());
        order.setUnitNumber(sa.getUnitNumber());
        order.setUnitCompany(sa.getUnitCompany());
        order.setRoomNumber(sa.getRoomNumber());
        order.setResidence(sa.getResidence());
        order.setLivingRoom(sa.getLivingRoom());
        order.setToilet(sa.getToilet());
        order.setRoomType(sa.getRoomType());
        order.setBedroomType(sa.getBedroomType());
        order.setDeposit(sa.getExpectedPrice());
        int num = 0;
        int paymentMethod = sa.getPaymentMethod();
        order.setPaymentMethod(paymentMethod);
        if (paymentMethod == 0) {
            num = 1;
        }
        if (paymentMethod == 1 || paymentMethod == 2) {
            num = 3;
        }
        if (paymentMethod == 3) {
            num = 6;
        }
        if (paymentMethod == 4) {
            num = 12;
        }
        order.setMoney(num * sa.getExpectedPrice());
        order.setPrice(sa.getExpectedPrice() + num * sa.getExpectedPrice());
        order.setAddTime(new Date());
        order.setDuration(num);
        order.setRentMoney(num * sa.getExpectedPrice());
        rentAhouseOrderService.addOrders(order);
        map.put("infoId", order.getNo());

        //放入缓存
        // 付款超时 45分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(order);
        redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + order.getMyId() + "_" + order.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_45);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData houseOrdersDetails(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        RentAhouseOrder io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_RENTAHOUSE_ORDER + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = rentAhouseOrderService.findNo(no);
            if (io == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
//            UserInfo userInfo = null;
//            userInfo = userInfoUtils.getUserInfo(io.getUserId() == CommonUtils.getMyId() ? io.getUserId() : io.getMyId());
//            if (userInfo != null) {
//                io.setName(userInfo.getName());
//                io.setHead(userInfo.getHead());
//                io.setProTypeId(userInfo.getProType());
//                io.setHouseNumber(userInfo.getHouseNumber());
//            }
            //放入缓存
            ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 分页查询订单列表
     * @param ordersType 订单类型: -1默认全部 0买房  1租房
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findHouseOrdersList(@PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<RentAhouseOrder> pageBean;
        pageBean = rentAhouseOrderService.findOrderList(CommonUtils.getMyId(), ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
//        RentAhouseOrder t = null;
//        UserInfo userCache = null;
//        if (list != null && list.size() > 0) {
//            for (int i = 0; i < list.size(); i++) {
//                t = (RentAhouseOrder) list.get(i);
//                if (t != null) {
//                    if (identity == 1) {
//                        userCache = userInfoUtils.getUserInfo(t.getUserId());
//                    } else {
//                        userCache = userInfoUtils.getUserInfo(t.getMyId());
//                    }
//                    if (userCache != null) {
//                        t.setName(userCache.getName());
//                        t.setHead(userCache.getHead());
//                        t.setProTypeId(userCache.getProType());
//                        t.setHouseNumber(userCache.getHouseNumber());
//                    }
//                }
//            }
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }
}
