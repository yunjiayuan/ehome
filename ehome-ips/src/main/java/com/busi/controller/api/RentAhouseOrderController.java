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
import java.text.SimpleDateFormat;
import java.util.*;

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
            Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_RENTAHOUSE_ORDER + order.getNo());
            if (ordersMap == null || ordersMap.size() <= 0) {
                io = rentAhouseOrderService.findNo(order.getNo());
                if (io == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
                }
                //放入缓存
                ordersMap = CommonUtils.objectToMap(io);
                redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + order.getNo(), ordersMap, Constants.USER_TIME_OUT);
            }
            RentAhouseOrder ahouseOrder = (RentAhouseOrder) CommonUtils.mapToObject(ordersMap, RentAhouseOrder.class);
            if (ahouseOrder == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            ahouseOrder.setRenewalState(1);
            ahouseOrder.setMakeMoneyStatus(0);
            int num = 0;
            int paymentMethod = ahouseOrder.getPaymentMethod();  //支付方式 0押一付一  1押一付三  2押一半年付  3押一年付
            ahouseOrder.setPaymentMethod(paymentMethod);
            if (paymentMethod == 0) {
                num = 1;
            }
            if (paymentMethod == 1) {
                num = 3;
            }
            if (paymentMethod == 2) {
                num = 6;
            }
            if (paymentMethod == 3) {
                num = 12;
            }
            ahouseOrder.setPrice(num * ahouseOrder.getMoney());//本次支付总金额
            ahouseOrder.setDuration(ahouseOrder.getDuration() + num); //已累计支付房租时长
            ahouseOrder.setRentMoney(num * ahouseOrder.getMoney() + ahouseOrder.getRentMoney());//已累计支付房租金额
            rentAhouseOrderService.upOrders(ahouseOrder);
            map.put("infoId", ahouseOrder.getNo());
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE_ORDER + ahouseOrder.getNo(), 0);

            //放入缓存
            // 付款超时 45分钟
            redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + ahouseOrder.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_45);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
        }

        //*******第一次下单********
        RentAhouse sa = communityService.findRentAhouse(order.getHouseId());
        if (sa == null || sa.getSellState() == 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "房源不存在", new JSONObject());
        }
        if (sa.getUserId() == CommonUtils.getMyId()) {//不能购买自己的
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "不能购买自己的", new JSONObject());
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
        order.setHousingArea(sa.getHousingArea());
        order.setOrientation(sa.getOrientation());
        int num = 0;
        int paymentMethod = sa.getPaymentMethod();
        order.setPaymentMethod(paymentMethod);
        if (paymentMethod == 0) {
            num = 1;
        }
        if (paymentMethod == 1) {
            num = 3;
        }
        if (paymentMethod == 2) {
            num = 6;
        }
        if (paymentMethod == 3) {
            num = 12;
        }
        order.setDeposit(sa.getExpectedPrice());
        order.setMoney(sa.getExpectedPrice());
        order.setPrice((num + 1) * sa.getExpectedPrice());
        order.setAddTime(new Date());
        order.setDuration(num);
        order.setRentMoney(num * sa.getExpectedPrice());

        if (order.getRoomState() == 1) {
            //下次支付时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date date = new Date();
            System.out.println(df.format(date)); // 当前系统时间        
            Date newDate = stepMonth(date, num);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            calendar.set(Calendar.HOUR_OF_DAY, 12); // 控制时
            calendar.set(Calendar.MINUTE, 0);       // 控制分
            calendar.set(Calendar.SECOND, 0);       // 控制秒
            order.setNextPaymentTime(calendar.getTime());
        }
        rentAhouseOrderService.addOrders(order);

        //更新房源状态为已出租
        RentAhouse rentAhouse = new RentAhouse();
        rentAhouse.setSellState(1);
        rentAhouse.setId(order.getHouseId());
        communityService.changeCommunityState(rentAhouse);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE + rentAhouse.getId(), 0);

        //放入缓存
        // 付款超时 15分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(order);
        redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + order.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_15);

        map.put("infoId", order.getNo());
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
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_RENTAHOUSE_ORDER + no);
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
            redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE_ORDER + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 分页查询订单列表
     * @param type  房屋类型: 0购房  1租房
     * @param ordersType 订单类型:  type=0时：0购房  1出售  type=1时：0租房  1出租
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findHouseOrdersList(@PathVariable int type, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<RentAhouseOrder> pageBean;
        pageBean = rentAhouseOrderService.findOrderList(CommonUtils.getMyId(), type, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

    //在原有的时间上添加几个月
    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

}
