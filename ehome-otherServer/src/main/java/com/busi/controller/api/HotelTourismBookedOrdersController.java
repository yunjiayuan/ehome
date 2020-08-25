package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.*;
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
 * @description: 酒店景区订座订单
 * @author: ZHaoJiaJie
 * @create: 2020-08-20 15:04:40
 */
@RestController
public class HotelTourismBookedOrdersController extends BaseController implements HotelTourismBookedOrdersApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HotelService hotelService;

    @Autowired
    TravelService travelService;

    @Autowired
    KitchenBookedService kitchenBookedService;

    @Autowired
    HotelTourismBookedOrdersService hotelTourismBookedOrdersService;

    @Autowired
    HotelTourismService hotelTourismService;

    /***
     * 新增订单
     * @param tourismBookedOrders
     * @return
     */
    @Override
    public ReturnData addHotelTourismOrder(@Valid @RequestBody HotelTourismBookedOrders tourismBookedOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String dishame = "";
        String dishes = "";
        double money = 0.0;
        Date date = new Date();
        KitchenReserveDishes laf = null;
        KitchenReserveDishes dis = null;
        List iup = null;
        int type = tourismBookedOrders.getType();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> kitchenMap = new HashMap<>();
        if (CommonUtils.checkFull(tourismBookedOrders.getGoodsIds()) || CommonUtils.checkFull(tourismBookedOrders.getFoodNumber())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        String[] sd = tourismBookedOrders.getGoodsIds().split(",");//菜品ID
        String[] fn = tourismBookedOrders.getFoodNumber().split(",");//菜品数量
        if (sd == null || fn == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        iup = kitchenBookedService.findDishesList(sd, type);
        if (iup == null || iup.size() <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,菜品不存在", new JSONObject());
        }
        if (type == 0) {// 所属类型：0酒店 1景区
            //查询酒店 缓存中不存在 查询数据库
            kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOTEL + tourismBookedOrders.getUserId());
            if (kitchenMap == null || kitchenMap.size() <= 0) {
                Hotel kitchen = hotelService.findReserve(tourismBookedOrders.getUserId());
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,酒店不存在", new JSONObject());
                }
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen);
                redisUtils.hmset(Constants.REDIS_KEY_HOTEL + kitchen.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
            Hotel kh = (Hotel) CommonUtils.mapToObject(kitchenMap, Hotel.class);
            if (kh == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,酒店不存在", new JSONObject());
            }
            laf = (KitchenReserveDishes) iup.get(0);
            if (laf == null || tourismBookedOrders.getMyId() == laf.getUserId() || iup.size() != sd.length) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
            }
            for (int i = 0; i < iup.size(); i++) {
                dis = (KitchenReserveDishes) iup.get(i);
                for (int j = 0; j < sd.length; j++) {
                    if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前菜品ID
                        double cost = dis.getCost();//单价
                        dishame = dis.getDishame();//菜名
                        dishes += dis.getId() + "," + dishame + "," + Integer.parseInt(fn[j]) + "," + cost + (i == iup.size() - 1 ? "" : ";");//菜品ID,菜名,数量,价格;
                        money += Integer.parseInt(fn[j]) * cost;//总价格
                    }
                }
            }
            long time = new Date().getTime();
            String noTime = String.valueOf(time);
            String random = CommonUtils.getRandom(6, 1);
            String noRandom = CommonUtils.strToMD5(noTime + tourismBookedOrders.getMyId() + random, 16);

            tourismBookedOrders.setNo(noRandom);//订单编号【MD5】
            tourismBookedOrders.setAddTime(date);
            tourismBookedOrders.setKitchenId(kh.getId());
            tourismBookedOrders.setKitchenName(kh.getHotelName());
            if (!CommonUtils.checkFull(kh.getPicture())) {
                String[] strings = kh.getPicture().split(",");
                tourismBookedOrders.setSmallMap(strings[0]);
            }
            tourismBookedOrders.setDishameCost(dishes);//菜名,数量,价格
            tourismBookedOrders.setMoney(money);//总价
            tourismBookedOrders.setOrdersType(1);

            hotelTourismBookedOrdersService.addOrders(tourismBookedOrders);
            map.put("infoId", tourismBookedOrders.getNo());

            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(tourismBookedOrders);
            redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + tourismBookedOrders.getMyId() + "_" + tourismBookedOrders.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        if (type == 1) {// 所属类型：0酒店 1景区
            //查询酒店 缓存中不存在 查询数据库
            kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + tourismBookedOrders.getUserId());
            if (kitchenMap == null || kitchenMap.size() <= 0) {
                ScenicSpot kitchen = travelService.findReserve(tourismBookedOrders.getUserId());
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,景区不存在", new JSONObject());
                }
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
            ScenicSpot kh = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
            if (kh == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,景区不存在", new JSONObject());
            }
            laf = (KitchenReserveDishes) iup.get(0);
            if (laf == null || tourismBookedOrders.getMyId() == laf.getUserId() || iup.size() != sd.length) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
            }
            for (int i = 0; i < iup.size(); i++) {
                dis = (KitchenReserveDishes) iup.get(i);
                for (int j = 0; j < sd.length; j++) {
                    if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前菜品ID
                        double cost = dis.getCost();//单价
                        dishame = dis.getDishame();//菜名
                        dishes += dis.getId() + "," + dishame + "," + Integer.parseInt(fn[j]) + "," + cost + (i == iup.size() - 1 ? "" : ";");//菜品ID,菜名,数量,价格;
                        money += Integer.parseInt(fn[j]) * cost;//总价格
                    }
                }
            }
            long time = new Date().getTime();
            String noTime = String.valueOf(time);
            String random = CommonUtils.getRandom(6, 1);
            String noRandom = CommonUtils.strToMD5(noTime + tourismBookedOrders.getMyId() + random, 16);

            tourismBookedOrders.setNo(noRandom);//订单编号【MD5】
            tourismBookedOrders.setAddTime(date);
            tourismBookedOrders.setKitchenId(kh.getId());
            tourismBookedOrders.setKitchenName(kh.getScenicSpotName());
            if (!CommonUtils.checkFull(kh.getPicture())) {
                String[] strings = kh.getPicture().split(",");
                tourismBookedOrders.setSmallMap(strings[0]);
            }
            tourismBookedOrders.setDishameCost(dishes);//菜名,数量,价格
            tourismBookedOrders.setMoney(money);//总价
            tourismBookedOrders.setOrdersType(1);

            hotelTourismBookedOrdersService.addOrders(tourismBookedOrders);
            map.put("infoId", tourismBookedOrders.getNo());

            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(tourismBookedOrders);
            redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + tourismBookedOrders.getMyId() + "_" + tourismBookedOrders.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 加菜（更新订单）
     * @param tourismBookedOrders
     * @return
     */
    @Override
    public ReturnData addHotelTourismToFood(@Valid @RequestBody HotelTourismBookedOrders tourismBookedOrders, BindingResult bindingResult) {
        HotelTourismBookedOrders io = hotelTourismBookedOrdersService.findById(tourismBookedOrders.getId(), CommonUtils.getMyId(), 5);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (CommonUtils.checkFull(tourismBookedOrders.getGoodsIds()) || CommonUtils.checkFull(tourismBookedOrders.getFoodNumber())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] sd = tourismBookedOrders.getGoodsIds().split(",");//菜品ID
        String[] fn = tourismBookedOrders.getFoodNumber().split(",");//菜品数量
        if (sd == null || fn == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        List iup = hotelService.findDishesList(sd);
        if (iup == null || iup.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        KitchenReserveDishes laf = (KitchenReserveDishes) iup.get(0);
        if (laf == null || io.getMyId() == laf.getUserId()) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String dishame = "";
        String dishes = "";
        double money = 0.00;
        KitchenReserveDishes dis = null;
        for (int i = 0; i < iup.size(); i++) {
            dis = (KitchenReserveDishes) iup.get(i);
            for (int j = 0; j < sd.length; j++) {
                if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前菜品ID
                    double cost = dis.getCost();//单价
                    dishame = dis.getDishame();//菜名
                    dishes += dis.getId() + "," + dishame + "," + Integer.parseInt(fn[j]) + "," + cost + (i == iup.size() - 1 ? "" : ";");//菜品ID,菜名,数量,价格;
                    money += Integer.parseInt(fn[j]) * cost;//总价格
                }
            }
        }
        if (io.getPaymentStatus() == 1) {//已支付
            io.setAddToFoodMoney(money);//加菜价格
        } else {
            if (io.getAddToFoodMoney() > 0) {//支付时判断是否为加菜支付
                io.setAddToFoodMoney(money + io.getAddToFoodMoney());//多次加菜价格
            }
        }
        io.setPaymentStatus(0);//每次加菜后重置支付状态为未支付（支付时判断用）
        io.setMoney(money + io.getMoney());//总价格
        if (CommonUtils.checkFull(io.getAddToFood())) {
            io.setAddToFood(dishes);//菜名,数量,价格
        } else {
            io.setAddToFood(io.getAddToFood() + ";" + dishes);//菜名,数量,价格
        }
        hotelTourismBookedOrdersService.upOrders(io);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", io.getNo());
        //清除缓存中的酒店订座订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), 0);
        //放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delHotelTourismOrder(@PathVariable long id) {
        HotelTourismBookedOrders io = hotelTourismBookedOrdersService.findById(id, CommonUtils.getMyId(), 0);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersState() != 0) {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? (io.getOrdersState() == 1 ? 3 : 2) : (io.getOrdersState() == 2 ? 3 : 1));
        } else {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? 2 : 1);
        }
        io.setUpdateCategory(0);
        hotelTourismBookedOrdersService.updateOrders(io);
        //清除缓存中的酒店订座订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由未接单改为已接单
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData receiptHotelTourism(@PathVariable long id) {
        HotelTourismBookedOrders io = hotelTourismBookedOrdersService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由未接单改为制作中【已接单】
        if (io.getUserId() == CommonUtils.getMyId()) {
            io.setOrdersType(2);        //已接单
            io.setOrderTime(new Date());
            io.setUpdateCategory(1);
            hotelTourismBookedOrdersService.updateOrders(io);

            //更新酒店景区订座剩余数量（-1）
            KitchenReserveBooked booked = hotelTourismService.findByUserId(io.getUserId(), io.getType());
            if (booked != null) {
                if (io.getPosition() == 0) {//就餐位置  0大厅  1包间
                    booked.setLooseTableTotal(booked.getLooseTableTotal() - 1);
                } else if (io.getPosition() == 1) {
                    booked.setRoomsTotal(booked.getRoomsTotal() - 1);
                }
                hotelTourismService.updatePosition(booked);
            }
            //清除缓存中的酒店订座订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //酒店订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由已接单改为菜已上桌（同进餐中）
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData upperHotelTourismTable(@PathVariable long id) {
        HotelTourismBookedOrders io = hotelTourismBookedOrdersService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由已接单改为菜已上桌
        io.setOrdersType(3); //菜已上桌（同进餐中）
        io.setUpperTableTime(new Date());
        io.setUpdateCategory(2);
        hotelTourismBookedOrdersService.updateOrders(io);

        //清除缓存中的酒店订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + CommonUtils.getMyId() + "_" + io.getNo(), 0);
        //酒店订单放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + CommonUtils.getMyId() + "_" + io.getNo(), ordersMap, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由进餐中改为完成
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData completeHotelTourism(@PathVariable long id) {
        HotelTourismBookedOrders io = hotelTourismBookedOrdersService.findById(id, CommonUtils.getMyId(), 3);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由已接单改为已完成
        io.setOrdersType(5);        //已完成
        io.setCompleteTime(new Date());
        io.setUpdateCategory(5);
        hotelTourismBookedOrdersService.updateOrders(io);
        //更新酒店景区订座剩余数量（+1）
        KitchenReserveBooked booked = hotelTourismService.findByUserId(io.getUserId(), io.getType());
        if (booked != null) {
            if (io.getPosition() == 0) {//就餐位置  0大厅  1包间
                booked.setLooseTableTotal(booked.getLooseTableTotal() + 1);
            } else {
                booked.setRoomsTotal(booked.getRoomsTotal() + 1);
            }
            hotelTourismService.updatePosition(booked);
        }
        //清除缓存中的酒店订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + CommonUtils.getMyId() + "_" + io.getNo(), 0);
        //酒店订单放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + CommonUtils.getMyId() + "_" + io.getNo(), ordersMap, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData findHotelTourismOrder(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        HotelTourismBookedOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = hotelTourismBookedOrdersService.findNo(no);
            if (io == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            UserInfo userInfo = null;
            userInfo = userInfoUtils.getUserInfo(io.getUserId() == CommonUtils.getMyId() ? io.getUserId() : io.getMyId());
            if (userInfo != null) {
                io.setName(userInfo.getName());
                io.setHead(userInfo.getHead());
                io.setProTypeId(userInfo.getProType());
                io.setHouseNumber(userInfo.getHouseNumber());
            }
            //放入缓存
            ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @Override
    public ReturnData cancelHotelTourismOrders(@PathVariable long id) {
        HotelTourismBookedOrders ko = null;
        ko = hotelTourismBookedOrdersService.findById(id, CommonUtils.getMyId(), 6);
        if (ko == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        //商家取消订单
        if (ko.getUserId() == CommonUtils.getMyId()) {
            if (ko.getOrdersType() < 3 && ko.getOrdersType() > 1) {
                ko.setOrdersType(8);
            }
        }
        if (ko.getMyId() == CommonUtils.getMyId()) {//用户可以取消商家未接单状态的单子
            if (ko.getOrdersType() == 1) {
                ko.setOrdersType(9);
            }
        }
        ko.setUpdateCategory(6);
        hotelTourismBookedOrdersService.updateOrders(ko);//更新订单
        if (ko.getOrdersType() == 8) {
            //更新缓存、钱包、账单
            if (ko.getMoney() > 0) {
                mqUtils.sendPurseMQ(ko.getMyId(), 26, 0, ko.getMoney());
            }
            //清除缓存中的酒店订座订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + ko.getMyId() + "_" + ko.getNo(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
            redisUtils.hmset(Constants.REDIS_KEY_HOTELTOURISMBOOKEDORDERS + ko.getMyId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 统计各类订单数量
     * @param type        :0酒店 1景区
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData countHotelTourismOrders(@PathVariable int type, @PathVariable int identity) {
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;
        int orderCont5 = 0;
        int orderCont6 = 0;
        int orderCont7 = 0;
        int orderCont8 = 0;
        int orderCont9 = 0;
        int orderCont10 = 0;

        HotelTourismBookedOrders kh = null;
        List list = null;
        list = hotelTourismBookedOrdersService.findIdentity(type, identity, CommonUtils.getMyId());//全部
        for (int i = 0; i < list.size(); i++) {
            kh = (HotelTourismBookedOrders) list.get(i);
            switch (kh.getOrdersType()) {
                case 1://未接单
                    orderCont1++;
                    orderCont0++;
                    break;
                case 2://已接单
                    orderCont2++;
                    orderCont0++;
                    break;
                case 3://菜已上桌
                    orderCont3++;
                    orderCont0++;
                    break;
                case 5://进餐完成
                    orderCont5++;
                    orderCont0++;
                    break;
                case 6://已清桌
                    orderCont6++;
                    orderCont0++;
                    break;
                case 7://接单超时
                    orderCont7++;
                    orderCont0++;
                    break;
                case 8://卖家取消订单
                    orderCont8++;
                    orderCont0++;
                    break;
                case 9://用户取消订单
                    orderCont9++;
                    orderCont0++;
                    break;
                case 10://已评价
                    orderCont10++;
                    orderCont0++;
                    break;
                default:
                    break;
            }
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("orderCont0", orderCont0);
        map.put("orderCont1", orderCont1);
        map.put("orderCont2", orderCont2);
        map.put("orderCont3", orderCont3);
        map.put("orderCont5", orderCont5);
        map.put("orderCont6", orderCont6);
        map.put("orderCont7", orderCont7);
        map.put("orderCont8", orderCont8);
        map.put("orderCont9", orderCont9);
        map.put("orderCont10", orderCont10);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 订单管理条件查询
     * @param type        :0酒店 1景区
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型: 0全部 1未接单,2已接单,3进餐中，4完成  5退款
     * @return
     */
    @Override
    public ReturnData findHotelTourismOrderList(@PathVariable int type, @PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HotelTourismBookedOrders> pageBean;
        pageBean = hotelTourismBookedOrdersService.findOrderList(type, identity, userId, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        HotelTourismBookedOrders t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (HotelTourismBookedOrders) list.get(i);
                if (t != null) {
                    if (identity == 1) {
                        userCache = userInfoUtils.getUserInfo(t.getUserId());
                    } else {
                        userCache = userInfoUtils.getUserInfo(t.getMyId());
                    }
                    if (userCache != null) {
                        t.setName(userCache.getName());
                        t.setHead(userCache.getHead());
                        t.setProTypeId(userCache.getProType());
                        t.setHouseNumber(userCache.getHouseNumber());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }
}
