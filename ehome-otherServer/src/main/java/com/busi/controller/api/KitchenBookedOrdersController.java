package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.KitchenBookedOrdersService;
import com.busi.service.KitchenBookedService;
import com.busi.service.KitchenService;
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
 * @description: 厨房订座订单
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 17:55
 */
@RestController
public class KitchenBookedOrdersController extends BaseController implements KitchenBookedOrdersApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    KitchenBookedService kitchenBookedService;

    @Autowired
    KitchenBookedOrdersService kitchenBookedOrdersService;

    /***
     * 新增订单
     * @param kitchenBookedOrders
     * @return
     */
    @Override
    public ReturnData addBookedOrder(@Valid @RequestBody KitchenBookedOrders kitchenBookedOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String dishame = "";
        String dishes = "";
        Date date = new Date();
        KitchenDishes laf = null;
        KitchenDishes dis = null;
        List iup = null;
        double money = 0.0;
        Map<String, Object> map = new HashMap<>();
        //查询厨房 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + kitchenBookedOrders.getUserId() + "_" + 1);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Kitchen kitchen = kitchenService.findByUserId(kitchenBookedOrders.getUserId(), 1);
            if (kitchen == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,厨房不存在", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 1, kitchenMap, Constants.USER_TIME_OUT);
        }
        Kitchen kh = (Kitchen) CommonUtils.mapToObject(kitchenMap, Kitchen.class);
        if (!CommonUtils.checkFull(kitchenBookedOrders.getGoodsIds()) && !CommonUtils.checkFull(kitchenBookedOrders.getFoodNumber())) {
            String[] sd = kitchenBookedOrders.getGoodsIds().split(",");//菜品ID
            String[] fn = kitchenBookedOrders.getFoodNumber().split(",");//菜品数量
            if (sd != null && fn != null) {
                iup = kitchenService.findDishesList(sd);
                if (iup != null && iup.size() > 0) {
                    laf = (KitchenDishes) iup.get(0);
                    if (laf != null && kitchenBookedOrders.getMyId() != laf.getUserId() && iup.size() == sd.length) {
                        for (int i = 0; i < iup.size(); i++) {
                            dis = (KitchenDishes) iup.get(i);
                            for (int j = 0; j < sd.length; j++) {
                                if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前菜品ID
                                    double cost = dis.getCost();//单价
                                    dishame = dis.getDishame();//菜名
                                    dishes += dis.getId() + "," + dishame + "," + Integer.parseInt(fn[j]) + "," + cost + (i == iup.size() - 1 ? "" : ";");//菜品ID,菜名,数量,价格;
                                    money += Integer.parseInt(fn[j]) * cost;//总价格
                                }
                            }
                        }
                    }
                }
            }
        }
        if (kh != null) {
            long time = new Date().getTime();
            String noTime = String.valueOf(time);
            String random = CommonUtils.getRandom(6, 1);
            String noRandom = CommonUtils.strToMD5(noTime + kitchenBookedOrders.getMyId() + random, 16);

            kitchenBookedOrders.setNo(noRandom);//订单编号【MD5】
            kitchenBookedOrders.setDishameCost(dishes);//菜名,数量,价格
            kitchenBookedOrders.setAddTime(date);
            kitchenBookedOrders.setKitchenId(kh.getId());
            kitchenBookedOrders.setMoney(money);//总价
//            kitchenBookedOrders.setUserId(kh.getUserId());
            kitchenBookedOrders.setKitchenName(kh.getKitchenName());
            kitchenBookedOrders.setSmallMap(kh.getKitchenCover());

            kitchenBookedOrdersService.addOrders(kitchenBookedOrders);

            map.put("infoId", kitchenBookedOrders.getNo());

            //放入缓存
            // 付款超时 15分钟
            Map<String, Object> ordersMap = CommonUtils.objectToMap(kitchenBookedOrders);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + kitchenBookedOrders.getMyId() + "_" + kitchenBookedOrders.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_15);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delBookedOrder(@PathVariable long id) {
        KitchenBookedOrders io = kitchenBookedOrdersService.findById(id, CommonUtils.getMyId(), 0);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersState() != 0) {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? (io.getOrdersState() == 1 ? 3 : 2) : (io.getOrdersState() == 2 ? 3 : 1));
        } else {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? 2 : 1);
        }
        io.setUpdateCategory(0);
        kitchenBookedOrdersService.updateOrders(io);
        //清除缓存中的厨房订座订单信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由未接单改为已接单
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData receiptBooked(@PathVariable long id) {
        KitchenBookedOrders io = kitchenBookedOrdersService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由未接单改为制作中【已接单】
        if (io.getUserId() == CommonUtils.getMyId()) {
            io.setOrdersType(2);        //已接单
            io.setOrderTime(new Date());
            io.setUpdateCategory(1);
            kitchenBookedOrdersService.updateOrders(io);

            //更新厨房订座剩余数量（-1）
            KitchenBooked booked = kitchenBookedService.findByUserId(io.getUserId());
            if (booked != null) {
                if (io.getPosition() == 0) {//就餐位置  0大厅  1包间
                    booked.setLooseTableTotal(booked.getLooseTableTotal() - 1);
                } else if (io.getPosition() == 1) {
                    booked.setRoomsTotal(booked.getRoomsTotal() - 1);
                }
                kitchenBookedService.updatePosition(booked);
            }
            //清除缓存中的厨房订座订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //厨房订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由已接单改为已完成
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData endBooked(@PathVariable long id) {
        KitchenBookedOrders io = kitchenBookedOrdersService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由已接单改为已完成
        if (io.getMyId() == CommonUtils.getMyId()) {
            io.setOrdersType(3);        //已完成
            io.setCompleteTime(new Date());
            io.setUpdateCategory(2);
            kitchenBookedOrdersService.updateOrders(io);

            //更新厨房订座剩余数量（+1）
            KitchenBooked booked = kitchenBookedService.findByUserId(io.getUserId());
            if (booked != null) {
                if (io.getPosition() == 0) {//就餐位置  0大厅  1包间
                    booked.setLooseTableTotal(booked.getLooseTableTotal() + 1);
                } else {
                    booked.setRoomsTotal(booked.getRoomsTotal() + 1);
                }
                kitchenBookedService.updatePosition(booked);
            }
            //更新厨房总销量
            Kitchen kh = kitchenService.findById(io.getKitchenId());
            kh.setTotalSales(kh.getTotalSales() + 1);
            kitchenService.updateNumber(kh);
            //清除缓存中厨房的信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kh.getUserId() + "_" + 1, 0);
            //清除缓存中的厨房订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + CommonUtils.getMyId() + "_" + io.getNo(), 0);
            //厨房订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + CommonUtils.getMyId() + "_" + io.getNo(), ordersMap, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData findBookedOrder(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        KitchenBookedOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = kitchenBookedOrdersService.findNo(no);
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
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @Override
    public ReturnData cancelBookedOrders(@PathVariable long id) {
        KitchenBookedOrders ko = null;
        ko = kitchenBookedOrdersService.findById(id, CommonUtils.getMyId(), 3);
        if (ko == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        //商家取消订单
        if (ko.getUserId() == CommonUtils.getMyId()) {
            if (ko.getOrdersType() < 3 && ko.getOrdersType() > 0) {
                ko.setOrdersType(6);
            }
        }
        if (ko.getMyId() == CommonUtils.getMyId()) {//用户可以取消商家未接单状态的单子
            if (ko.getOrdersType() == 1) {
                ko.setOrdersType(7);
            }
        }
        ko.setUpdateCategory(3);
        kitchenBookedOrdersService.updateOrders(ko);//更新订单
        if (ko.getOrdersType() == 6 || ko.getOrdersType() == 7) {
            //更新缓存、钱包、账单
            if (ko.getMoney() > 0) {
                mqUtils.sendPurseMQ(ko.getMyId(), 26, 0, ko.getMoney());
            }
            //清除缓存中的厨房订座订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + ko.getMyId() + "_" + ko.getNo(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + ko.getMyId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData countBookedOrders(@PathVariable int identity) {
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;
        int orderCont4 = 0;
        int orderCont5 = 0;
        int orderCont6 = 0;
        int orderCont7 = 0;
        int orderCont8 = 0;

        KitchenBookedOrders kh = null;
        List list = null;
        list = kitchenBookedOrdersService.findIdentity(identity, CommonUtils.getMyId());//全部
        for (int i = 0; i < list.size(); i++) {
            kh = (KitchenBookedOrders) list.get(i);
            switch (kh.getOrdersType()) {
                case 0://未付款
                    orderCont1++;
                    orderCont0++;//全部
                    break;
                case 1://未接单
                    orderCont2++;
                    orderCont0++;
                    break;
                case 2://已接单
                    orderCont3++;
                    orderCont0++;
                    break;
                case 3://已完成
                    orderCont4++;
                    orderCont0++;
                    break;
                case 4://接单超时订单
                    orderCont5++;
                    orderCont0++;
                    break;
                case 5://付款超时订单
                    orderCont6++;
                    orderCont0++;
                    break;
                case 6://卖家取消订单
                    orderCont7++;
                    orderCont0++;
                    break;
                case 7://用户取消订单
                    orderCont8++;
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
        map.put("orderCont4", orderCont4);
        map.put("orderCont5", orderCont5);
        map.put("orderCont6", orderCont6);
        map.put("orderCont7", orderCont7);
        map.put("orderCont8", orderCont8);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2已接单,3已完成  4卖家取消订单 5用户取消订单 6付款超时 7接单超时
     * @return
     */
    @Override
    public ReturnData findBookedOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenBookedOrders> pageBean;
        pageBean = kitchenBookedOrdersService.findOrderList(identity, userId, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        KitchenBookedOrders t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (KitchenBookedOrders) list.get(i);
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
