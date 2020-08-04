package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HotelOrderService;
import com.busi.service.HotelService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 家门口酒店民宿订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 17:06:12
 */
@RestController
public class HotelOrderController extends BaseController implements HotelOrderApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HotelService travelService;

    @Autowired
    HotelOrderService travelOrderService;

    /***
     * 新增订单
     * @param scenicSpotOrder
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHotelOrder(@Valid @RequestBody HotelOrder scenicSpotOrder, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String dishame = "";
        String dishes = "";
        double money = 0.0;
        Date date = new Date();
        HotelRoom laf = null;
        HotelRoom dis = null;
        List iup = null;
        Map<String, Object> map = new HashMap<>();
        //查询酒店民宿 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOTEL + scenicSpotOrder.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Hotel kitchen = travelService.findReserve(scenicSpotOrder.getUserId());
            if (kitchen == null) {
//                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,酒店民宿不存在", new JSONObject());
            }
            //放入缓存
//            kitchenMap = CommonUtils.objectToMap(kitchen);
//            redisUtils.hmset(Constants.REDIS_KEY_HOTEL + kitchen.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
        }
//        Hotel kh = (Hotel) CommonUtils.mapToObject(kitchenMap, Hotel.class);
//        if (kh == null) {
//            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,酒店民宿不存在", new JSONObject());
//        }
//        if (CommonUtils.checkFull(scenicSpotOrder.getTicketsIds()) || CommonUtils.checkFull(scenicSpotOrder.getTicketsNumber())) {
//            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,房间信息不可为空", new JSONObject());
//        }
        String[] sd = scenicSpotOrder.getTicketsIds().split(",");//房间ID
        String[] fn = scenicSpotOrder.getTicketsNumber().split(",");//房间数量
//        if (sd != null && fn != null) {
//            iup = travelService.findDishesList(sd);
//            if (iup == null || iup.size() <= 0) {
//                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,房间不存在", new JSONObject());
//            }
//            laf = (HotelRoom) iup.get(0);
//            if (laf == null || scenicSpotOrder.getMyId() == laf.getUserId() || iup.size() != sd.length) {
//                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,房间信息错误", new JSONObject());
//            }
//            for (int i = 0; i < iup.size(); i++) {
//                dis = (HotelRoom) iup.get(i);
//                for (int j = 0; j < sd.length; j++) {
//                    if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前房间ID
//                        double cost = dis.getCost();//单价
//                        dishame = dis.getName();//房间名称
//                        dishes += dis.getId() + "," + dishame + "," + Integer.parseInt(fn[j]) + "," + cost + (i == iup.size() - 1 ? "" : ";");//房间ID,名称,数量,价格;
//                        money += Integer.parseInt(fn[j]) * cost;//总价格
//                    }
//                }
//            }
//        }
        long time = date.getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + scenicSpotOrder.getMyId() + random, 16);
        scenicSpotOrder.setNo(noRandom);//订单编号【MD5】

        String random2 = CommonUtils.getRandom(6, 1);
        String noRandom2 = CommonUtils.strToMD5(noTime + scenicSpotOrder.getMyId() + random2, 16);
        scenicSpotOrder.setVoucherCode(noRandom2);//凭证码【MD5】
        scenicSpotOrder.setAddTime(date);
//        scenicSpotOrder.setHotelId(kh.getId());
//        scenicSpotOrder.setHotelName(kh.getHotelName());
//        scenicSpotOrder.setHotelType(kh.getHotelType());
//        if (!CommonUtils.checkFull(kh.getPicture())) {
//            String[] strings = kh.getPicture().split(",");
//            scenicSpotOrder.setSmallMap(strings[0]);
//        }
        scenicSpotOrder.setDishameCost(dishes);//名称,数量,价格
        scenicSpotOrder.setMoney(money);//总价
        travelOrderService.addOrders(scenicSpotOrder);
        map.put("infoId", scenicSpotOrder.getNo());

        //放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(scenicSpotOrder);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + scenicSpotOrder.getMyId() + "_" + scenicSpotOrder.getNo(), ordersMap, Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delHotelOrder(@PathVariable long id) {
        HotelOrder io = travelOrderService.findById(id, CommonUtils.getMyId(), 0);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersState() != 0) {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? (io.getOrdersState() == 1 ? 3 : 2) : (io.getOrdersState() == 2 ? 3 : 1));
        } else {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? 2 : 1);
        }
        io.setUpdateCategory(0);
        travelOrderService.updateOrders(io);
        //清除缓存中的酒店民宿订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELORDERS + io.getMyId() + "_" + io.getNo(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改订单状态
     * 由未验票改为已验票
     * @param id  订单Id
     * @param voucherCode  凭证码
     * @return
     */
    @Override
    public ReturnData receiptHotel(@PathVariable long id, @PathVariable String voucherCode) {
        HotelOrder io = travelOrderService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersType() == 1) {//防止多次验票成功后多次打款
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (io.getOrdersType() == 0) {//已付款未验票
            if (io.getUserId() != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限核验", new JSONObject());
            }
            if (!io.getVoucherCode().equals(voucherCode)) {
                return returnData(StatusCode.CODE_HOTEL_INVALID.CODE_VALUE, "房间凭证码无效", new JSONObject());
            }
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            //格式化为相同格式
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String DateStr1 = dateFormat.format(io.getCheckInTime());
            String DateStr2 = dateFormat.format(new Date());
            Date dateTime1 = null;
            try {
                dateTime1 = dateFormat.parse(DateStr1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dateTime2 = null;
            try {
                dateTime2 = dateFormat.parse(DateStr2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dateTime1 == null || dateTime2 == null) {
                return returnData(StatusCode.CODE_HOTEL_BE_OVERDUE.CODE_VALUE, "房间已过期", new JSONObject());
            }
            int i = dateTime1.compareTo(dateTime2);
            if (i > 0) {
                io.setOrdersType(6);
                //酒店民宿订单放入缓存
                redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
                return returnData(StatusCode.CODE_HOTEL_BE_OVERDUE.CODE_VALUE, "房间已过期", new JSONObject());
            } else if (i < 0) {
                return returnData(StatusCode.CODE_HOTEL_ADVANCE.CODE_VALUE, "入住日期未到", new JSONObject());
            } else {
                io.setOrdersType(1);
            }
            //由未验票改为已验票
            io.setInspectTicketTime(new Date());
            io.setUpdateCategory(1);
            travelOrderService.updateOrders(io);
            //商家入账
            mqUtils.sendPurseMQ(io.getUserId(), 38, 0, io.getMoney());
            //清除缓存中的酒店民宿 订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOTELORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //酒店民宿订单放入缓存
            redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改订单状态
     * 由已验票改为已完成
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData completeHotel(@PathVariable long id) {
        HotelOrder io = travelOrderService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由已验票改为已完成
        io.setOrdersType(2);        //已完成
        io.setCompleteTime(new Date());
        io.setUpdateCategory(2);
        travelOrderService.updateOrders(io);
        //清除缓存中的酒店民宿订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELORDERS + CommonUtils.getMyId() + "_" + io.getNo(), 0);
        //酒店民宿订单放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + CommonUtils.getMyId() + "_" + io.getNo(), ordersMap, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData findHotelOrder(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        HotelOrder io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_HOTELORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = travelOrderService.findNo(no);
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
            redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @Override
    public ReturnData cancelHotelOrders(@PathVariable long id) {
        HotelOrder ko = null;
        ko = travelOrderService.findById(id, CommonUtils.getMyId(), 4);
        if (ko == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        //商家取消订单
        if (ko.getUserId() == CommonUtils.getMyId()) {
            if (ko.getOrdersType() == 0) {
                ko.setOrdersType(4);
            }
        }
        if (ko.getMyId() == CommonUtils.getMyId()) {//用户可以取消商家未验票状态的单子
            long time = ko.getCheckInTime().getTime();
            long time2 = new Date().getTime();
            long time3 = 86400000;
            if (time - time3 < time2) {//可以取消一天前的
                if (ko.getOrdersType() == 0) {
                    ko.setOrdersType(5);
                }
            }
        }
        ko.setUpdateCategory(4);
        travelOrderService.updateOrders(ko);//更新订单
        if ((ko.getOrdersType() == 4 || ko.getOrdersType() == 5) && ko.getPaymentStatus() == 1) {
            //更新缓存、钱包、账单
            if (ko.getMoney() > 0) {
                mqUtils.sendPurseMQ(ko.getMyId(), 38, 0, ko.getMoney());
            }
            //清除缓存中的酒店民宿 订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOTELORDERS + ko.getMyId() + "_" + ko.getNo(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
            redisUtils.hmset(Constants.REDIS_KEY_HOTELORDERS + ko.getMyId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 订单管理条件查询
     * @param userId
     * @param identity     身份区分：1买家 2商家
     * @param ordersType   订单类型: -1全部 0未付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成未评价  4卖家取消订单 5用户取消订单 6已过期
     * @param page         当前查询数据的页码
     * @param count        每页的显示条数
     * @return
     */
    @Override
    public ReturnData findHotelOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HotelOrder> pageBean;
        pageBean = travelOrderService.findOrderList(identity, userId, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
//        HotelOrder t = null;
//        UserInfo userCache = null;
//        if (list != null && list.size() > 0) {
//            for (int i = 0; i < list.size(); i++) {
//                t = (HotelOrder) list.get(i);
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
