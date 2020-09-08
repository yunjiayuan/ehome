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
 * @description: 厨房订单
 * @author: ZHaoJiaJie
 * @create: 2019-03-06 14:46
 */
@RestController
public class KitchenOrdersController extends BaseController implements KitchenOrdersApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    KitchenOrdersService kitchenOrdersService;

    @Autowired
    KitchenBookedOrdersService kitchenBookedOrdersService;

    @Autowired
    ShippingAddressService shippingAddressService;

    @Autowired
    KitchenBookedService kitchenBookedService;

    /***
     * 新增订单
     * @param kitchenOrders
     * @return
     */
    @Override
    public ReturnData addKitchenOrders(@Valid @RequestBody KitchenOrders kitchenOrders, BindingResult bindingResult) {
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
        String[] sd = kitchenOrders.getGoodsIds().split(",");//菜品ID
        String[] fn = kitchenOrders.getFoodNumber().split(",");//菜品数量
        if (sd != null && fn != null) {
            iup = kitchenService.findDishesList(sd);
            if (iup != null && iup.size() > 0) {
                laf = (KitchenDishes) iup.get(0);
                if (laf != null && kitchenOrders.getMyId() != laf.getUserId() && iup.size() == sd.length) {
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
                    //查询缓存 缓存中不存在 查询数据库
                    Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + laf.getUserId() + "_" + 0);
                    if (kitchenMap == null || kitchenMap.size() <= 0) {
                        Kitchen kitchen = kitchenService.findByUserId(laf.getUserId());
                        if (kitchen == null) {
                            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,厨房不存在", new JSONObject());
                        }
                        //放入缓存
                        kitchenMap = CommonUtils.objectToMap(kitchen);
                        redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 0, kitchenMap, Constants.USER_TIME_OUT);
                    }
                    Kitchen kh = (Kitchen) CommonUtils.mapToObject(kitchenMap, Kitchen.class);
                    ShippingAddress s = shippingAddressService.findUserById(kitchenOrders.getAddressId());
                    if (kh != null && s != null) {
                        long time = new Date().getTime();
                        String noTime = String.valueOf(time);
                        String random = CommonUtils.getRandom(6, 1);
                        String noRandom = CommonUtils.strToMD5(noTime + kitchenOrders.getMyId() + random, 16);

                        kitchenOrders.setNo(noRandom);//订单编号【MD5】
                        kitchenOrders.setDishameCost(dishes);//菜名,数量,价格
                        kitchenOrders.setAddTime(date);
                        kitchenOrders.setAddressId(s.getId());
                        kitchenOrders.setKitchenId(kh.getId());
                        kitchenOrders.setMoney(money);//总价
                        kitchenOrders.setUserId(laf.getUserId());
                        kitchenOrders.setAddress(s.getAddress());
//                        kitchenOrders.setAddress_city(s.getCity());
//                        kitchenOrders.setAddress_district(s.getDistrict());
//                        kitchenOrders.setAddress_province(s.getProvince());
                        kitchenOrders.setKitchenName(kh.getKitchenName());
                        kitchenOrders.setAddress_Name(s.getContactsName());
                        kitchenOrders.setSmallMap(kh.getKitchenCover());
                        kitchenOrders.setAddress_Phone(s.getContactsPhone());
                        kitchenOrders.setAddress_postalcode(s.getPostalcode());

                        kitchenOrdersService.addOrders(kitchenOrders);

                        map.put("infoId", kitchenOrders.getNo());

                        //放入缓存
                        // 付款超时 15分钟
                        Map<String, Object> ordersMap = CommonUtils.objectToMap(kitchenOrders);
                        redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + kitchenOrders.getMyId() + "_" + kitchenOrders.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_15);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delKitchenOrders(@PathVariable long id) {
        KitchenOrders io = kitchenOrdersService.findById(id, CommonUtils.getMyId(), 0);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersState() != 0) {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? (io.getOrdersState() == 1 ? 3 : 2) : (io.getOrdersState() == 2 ? 3 : 1));
        } else {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? 2 : 1);
        }
        io.setUpdateCategory(0);
        kitchenOrdersService.updateOrders(io);
        //清除缓存中的厨房订单信息
        redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由未接单改为制作中【已接单】
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData kitchenReceipt(@PathVariable long id) {
        KitchenOrders io = kitchenOrdersService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由未接单改为制作中【已接单】
        if (io.getUserId() == CommonUtils.getMyId()) {
            io.setOrdersType(2);        //已接单
            io.setOrderTime(new Date());
            io.setUpdateCategory(1);
            kitchenOrdersService.updateOrders(io);
            //清除缓存中的嗯厨房订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //厨房订单放入缓存(六小时配送超时)
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.MSG_TIME_OUT_HOUR_1 * 6);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由制作中改为配送中
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData kitchenDelivery(@PathVariable long id) {
        KitchenOrders io = kitchenOrdersService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由制作中改为配送中
        long order = new Date().getTime() - io.getOrderTime().getTime();
        if (order > 3 * 24 * 60 * 60 * 1000 - 30 * 60 * 1000) {//30分钟时间差 给定时任务
            return returnData(StatusCode.CODE_ORDER_TIMEOUT.CODE_VALUE, "该订单已超时", new JSONObject());
        }
        if (io.getUserId() == CommonUtils.getMyId()) {
            io.setOrdersType(3);        //已发货
            io.setDeliveryTime(new Date());//发货时间
            io.setUpdateCategory(2);

            kitchenOrdersService.updateOrders(io);
            //清除缓存中的厨房订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //厨房订单放入缓存(收货超时24小时)
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_60_24_1);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由配送中改为已卖出
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData kitchenSell(@PathVariable long id) {
        KitchenOrders io = kitchenOrdersService.findById(id, CommonUtils.getMyId(), 3);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由配送中改为已卖出
        if (io.getUserId() == CommonUtils.getMyId()) {
            io.setOrdersType(4);        //已收货
            io.setReceivingTime(new Date());
            io.setUpdateCategory(3);
            kitchenOrdersService.updateOrders(io);

            Kitchen kh = kitchenService.findById(io.getKitchenId());
            kh.setTotalSales(kh.getTotalSales() + 1);
            kitchenService.updateNumber(kh);//更新厨房总销量
            //清除缓存中厨房的信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHEN + kh.getUserId() + "_" + 0, 0);
            //清除缓存中的厨房订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + CommonUtils.getMyId() + "_" + io.getNo(), 0);
            //厨房订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + CommonUtils.getMyId() + "_" + io.getNo(), ordersMap, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData findKitchenOrders(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        KitchenOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHENORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = kitchenOrdersService.findByNo(no);
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
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData countKitchenOrders(@PathVariable int identity) {
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;
        int orderCont4 = 0;
        int orderCont5 = 0;
        int orderCont6 = 0;
        int orderCont7 = 0;
        int orderCont8 = 0;
        int orderCont9 = 0;
        int orderCont10 = 0;
        int orderCont11 = 0;

        KitchenOrders kh = null;
        List list = null;
        list = kitchenOrdersService.findIdentity(identity, CommonUtils.getMyId());//全部
        for (int i = 0; i < list.size(); i++) {
            kh = (KitchenOrders) list.get(i);
            switch (kh.getOrdersType()) {
                case 0://未付款
                    orderCont1++;
                    orderCont0++;//全部
                    break;
                case 1://未接单
                    orderCont2++;
                    orderCont0++;
                    break;
                case 2://制作中
                    orderCont3++;
                    orderCont0++;
                    break;
                case 3://配送中
                    orderCont4++;
                    orderCont0++;
                    break;
                case 4://已卖出
                    orderCont5++;
                    orderCont0++;
                    break;
                case 5://卖家取消订单
                    orderCont6++;
                    orderCont0++;
                    break;
                case 6://付款超时订单
                    orderCont7++;
                    orderCont0++;
                    break;
                case 7://接单超时订单
                    orderCont8++;
                    orderCont0++;
                    break;
                case 8://发货超时订单
                    orderCont9++;
                    orderCont0++;
                    break;
                case 9://买家取消订单
                    orderCont10++;
                    orderCont0++;
                    break;
                case 10://已评价
                    orderCont11++;
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
        map.put("orderCont9", orderCont9);
        map.put("orderCont10", orderCont10);
        map.put("orderCont11", orderCont11);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @Override
    public ReturnData cancelKitchenOrders(@PathVariable long id) {
        KitchenOrders ko = null;
        ko = kitchenOrdersService.findById(id, CommonUtils.getMyId(), 4);
        if (ko == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        //商家取消订单
        if (ko.getUserId() == CommonUtils.getMyId()) {
            if (ko.getOrdersType() < 4 && ko.getOrdersType() > 0) {
                ko.setOrdersType(5);
            }
        }
        if (ko.getMyId() == CommonUtils.getMyId()) {//用户可以取消商家未接单状态的单子
            if (ko.getOrdersType() == 1) {
                ko.setOrdersType(9);
            }
        }
        ko.setUpdateCategory(4);
        kitchenOrdersService.updateOrders(ko);//更新订单
        if (ko.getOrdersType() == 5) {
            //更新缓存、钱包、账单
            mqUtils.sendPurseMQ(ko.getMyId(), 16, 0, ko.getMoney());
            //清除缓存中的厨房订单信息
            redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + ko.getMyId() + "_" + ko.getNo(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + ko.getMyId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 菜品点赞
     * @param infoId 订单
     * @return
     */
    @Override
    public ReturnData addDishesLike(@PathVariable long infoId, @PathVariable String dishesIds) {
        List list = null;
        KitchenDishes dis = null;
        KitchenOrders io = null;
        io = kitchenOrdersService.findById(infoId, CommonUtils.getMyId(), 5);
        if (io == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        list = kitchenService.findDishesList(dishesIds.split(","));
        if (list != null && list.size() > 0 && io != null) {
            String[] sd = dishesIds.split(",");//菜品ID
            String dishameCost = io.getDishameCost();
            if (dishameCost != null) {
                String[] dishame = dishameCost.split(";");
                if (list.size() == sd.length) {
                    for (int i = 0; i < list.size(); i++) {
                        dis = (KitchenDishes) list.get(i);
                        for (int j = 0; j < dishame.length; j++) {
                            String[] dishes = dishame[j].split(",");
                            long dishesId = Long.parseLong(dishes[0]);
                            if (dishesId == dis.getId()) {
                                KitchenFabulous like = new KitchenFabulous();
                                like.setMyId(CommonUtils.getMyId());
                                like.setUserId(dis.getUserId());
                                like.setDishesId(dis.getId());
                                like.setTime(new Date());
                                like.setStatus(0);    //0正常
                                kitchenOrdersService.addLike(like);

                                //更新菜品点赞数
                                dis.setPointNumber(dis.getPointNumber() + 1);
                                kitchenService.updateLike(dis);
                            }
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增评价
     * @param ev
     * @return
     */
    @Override
    public ReturnData addKitchenEvaluate(@Valid @RequestBody KitchenEvaluate ev, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        KitchenDishes dis = null;
        List list = null;
        if (ev.getBookedState() == 0) {// 是否订座  0厨房  1订座
            KitchenOrders io = kitchenOrdersService.findById(ev.getOrderId(), CommonUtils.getMyId(), 5);
            if (io != null && io.getOrdersType() == 4) {
                //查询缓存 缓存中不存在 查询数据库
                Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + io.getUserId() + "_" + 0);
                if (kitchenMap == null || kitchenMap.size() <= 0) {
                    Kitchen kitchen = kitchenService.findById(io.getKitchenId());
                    if (kitchen == null) {
                        return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,厨房不存在", new JSONObject());
                    }
                    //放入缓存
                    kitchenMap = CommonUtils.objectToMap(kitchen);
                    redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 0, kitchenMap, Constants.USER_TIME_OUT);
                }
                Kitchen kh = (Kitchen) CommonUtils.mapToObject(kitchenMap, Kitchen.class);
                if (kh != null) {
                    if (!CommonUtils.checkFull(ev.getDishesIds())) {
                        String[] sd = ev.getDishesIds().split(",");//菜品ID
                        list = kitchenService.findDishesList(sd);
                        if (list != null && list.size() > 0) {
                            String dishameCost = io.getDishameCost();
                            if (dishameCost != null) {
                                String[] dishame = dishameCost.split(";");
                                if (list.size() == sd.length) {
                                    for (int i = 0; i < list.size(); i++) {
                                        dis = (KitchenDishes) list.get(i);
                                        for (int j = 0; j < dishame.length; j++) {
                                            String[] dishes = dishame[j].split(",");
                                            long dishesId = Long.parseLong(dishes[0]);
                                            if (dishesId == dis.getId()) {
                                                KitchenFabulous like = new KitchenFabulous();
                                                like.setMyId(CommonUtils.getMyId());
                                                like.setUserId(dis.getUserId());
                                                like.setDishesId(dis.getId());
                                                like.setTime(new Date());
                                                like.setStatus(0);    //0正常
                                                like.setBookedState(0);
                                                kitchenOrdersService.addLike(like);

                                                //更新菜品点赞数
                                                dis.setPointNumber(dis.getPointNumber() + 1);
                                                kitchenService.updateLike(dis);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ev.setKitchenId(io.getKitchenId());
                    ev.setOrderId(io.getId());
                    ev.setUserId(CommonUtils.getMyId());
                    ev.setKitchenCover(kh.getKitchenCover());
                    ev.setTime(new Date());
                    kitchenOrdersService.addEvaluate(ev);

                    //更新评论平均分
                    List list1 = kitchenService.findKitchenList6(io.getKitchenId(), 0);
                    if (list1 != null && list1.size() > 0) {
                        long score = 0;//总分
                        double averageScore = 0;   // 平均评分
                        for (int i = 0; i < list1.size(); i++) {
                            KitchenEvaluate kitchenEvaluate = (KitchenEvaluate) list1.get(i);
                            if (kitchenEvaluate == null) {
                                continue;
                            }
                            score += kitchenEvaluate.getScore();
                        }
                        //更新厨房总评分
                        kh.setTotalScore(score);
                        //更新评论平均分
                        averageScore = score / list1.size();
                        kh.setAverageScore((int) Math.round(averageScore));
                        kitchenService.updateScore(kh);
                    }
                    io.setOrdersType(10);//更新订单状态为已评价
                    io.setUpdateCategory(5);
                    kitchenOrdersService.updateOrders(io);
                    //清除缓存中的厨房订单信息
                    redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), 0);
                    //放入缓存
                    Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
                    redisUtils.hmset(Constants.REDIS_KEY_KITCHENORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
                }
            }
        } else {
            KitchenBookedOrders io = kitchenBookedOrdersService.findById(ev.getOrderId(), CommonUtils.getMyId(), 7);
            if (io != null && io.getOrdersType() == 5) {
                //查询缓存 缓存中不存在 查询数据库
                Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_KITCHEN + io.getUserId() + "_" + 1);
                if (kitchenMap == null || kitchenMap.size() <= 0) {
                    KitchenReserve kitchen = kitchenBookedService.findReserve(io.getUserId());
                    if (kitchen == null) {
                        return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,厨房不存在", new JSONObject());
                    }
                    //放入缓存
                    kitchenMap = CommonUtils.objectToMap(kitchen);
                    redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + kitchen.getUserId() + "_" + 1, kitchenMap, Constants.USER_TIME_OUT);
                }
                KitchenReserve kh = (KitchenReserve) CommonUtils.mapToObject(kitchenMap, KitchenReserve.class);
                if (kh != null) {
                    if (!CommonUtils.checkFull(ev.getDishesIds())) {
                        String[] sd = ev.getDishesIds().split(",");//菜品ID
                        list = kitchenService.findDishesList(sd);
                        if (list != null && list.size() > 0) {
                            String dishameCost = io.getDishameCost();
                            if (dishameCost != null) {
                                String[] dishame = dishameCost.split(";");
                                if (list.size() == sd.length) {
                                    for (int i = 0; i < list.size(); i++) {
                                        dis = (KitchenDishes) list.get(i);
                                        for (int j = 0; j < dishame.length; j++) {
                                            String[] dishes = dishame[j].split(",");
                                            long dishesId = Long.parseLong(dishes[0]);
                                            if (dishesId == dis.getId()) {
                                                KitchenFabulous like = new KitchenFabulous();
                                                like.setMyId(CommonUtils.getMyId());
                                                like.setUserId(dis.getUserId());
                                                like.setDishesId(dis.getId());
                                                like.setTime(new Date());
                                                like.setStatus(0);    //0正常
                                                like.setBookedState(1);
                                                kitchenOrdersService.addLike(like);

                                                //更新菜品点赞数
                                                dis.setPointNumber(dis.getPointNumber() + 1);
                                                kitchenService.updateLike(dis);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ev.setKitchenId(io.getKitchenId());
                    ev.setOrderId(io.getId());
                    ev.setKitchenCover(kh.getKitchenCover());
                    ev.setTime(new Date());

                    kitchenOrdersService.addEvaluate(ev);

                    //更新评论平均分
                    List list1 = kitchenService.findKitchenList6(io.getKitchenId(), 1);
                    if (list1 != null && list1.size() > 0) {
                        long score = 0;//总分
                        double averageScore = 0;   // 平均评分
                        for (int i = 0; i < list1.size(); i++) {
                            KitchenEvaluate kitchenEvaluate = (KitchenEvaluate) list1.get(i);
                            if (kitchenEvaluate == null) {
                                continue;
                            }
                            score += kitchenEvaluate.getScore();
                        }
                        //更新总评分
                        kh.setTotalScore(score);
                        //更新评论平均分
                        averageScore = score / list1.size();
                        kh.setAverageScore((int) Math.round(averageScore));
                        kitchenBookedService.updateScore(kh);
                    }
                    io.setOrdersType(10);//更新订单状态为已评价
                    io.setUpdateCategory(6);
                    kitchenBookedOrdersService.updateOrders(io);
                    //清除缓存中的厨房订单信息
                    redisUtils.expire(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), 0);
                    //放入缓存
                    Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
                    redisUtils.hmset(Constants.REDIS_KEY_KITCHENBOOKEDORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除评价
     * @param id 评价ID
     * @return
     */
    @Override
    public ReturnData delKitchenEvaluate(@PathVariable long id) {
        kitchenOrdersService.findEvaluateId(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 条件查询订单
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2制作中(已接单未发货),3配送(已发货未收货),4已卖出(已收货未评价),  5卖家取消订单 6付款超时 7接单超时 8发货超时 9用户取消订单 10 已评价
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @return
     */
    @Override
    public ReturnData findKitchenOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenOrders> pageBean;
        pageBean = kitchenOrdersService.findOrderList(identity, userId, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        KitchenOrders t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (KitchenOrders) list.get(i);
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
