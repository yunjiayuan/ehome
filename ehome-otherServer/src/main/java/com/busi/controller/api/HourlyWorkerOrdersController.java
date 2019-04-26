package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HourlyWorkerOrdersService;
import com.busi.service.HourlyWorkerService;
import com.busi.service.ShippingAddressService;
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
 * @description: 小时工订单
 * @author: ZHaoJiaJie
 * @create: 2019-04-22 16:45
 */
@RestController
public class HourlyWorkerOrdersController extends BaseController implements HourlyWorkerOrdersApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HourlyWorkerService hourlyWorkerService;

    @Autowired
    HourlyWorkerOrdersService hourlyWorkerOrdersService;

    @Autowired
    ShippingAddressService shippingAddressService;

    /***
     * 新增小时工订单
     * @param hourlyWorkerOrders
     * @return
     */
    @Override
    public ReturnData addHourlyOrders(@Valid @RequestBody HourlyWorkerOrders hourlyWorkerOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Date date = new Date();
        List iup = null;
        String typeIds = "";
        HourlyWorkerType laf = null;
        HourlyWorkerType dis = null;
        Map<String, Object> map = new HashMap<>();
        String[] sd = hourlyWorkerOrders.getWorkerTypeIds().split(",");//工种ID
        iup = hourlyWorkerService.findDishesList(sd);
        if (iup != null && iup.size() > 0 && sd != null) {
            laf = (HourlyWorkerType) iup.get(0);
            if (laf != null && hourlyWorkerOrders.getMyId() != laf.getUserId() && iup.size() == sd.length) {
                for (int i = 0; i < iup.size(); i++) {
                    dis = (HourlyWorkerType) iup.get(i);
                    for (int j = 0; j < sd.length; j++) {
                        if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前工种ID
                            //更新工种服务次数
                            dis.setSales(dis.getSales() + 1);
                            hourlyWorkerService.updateType(dis);
                            String dishame = dis.getWorkerType();//工作类型
                            typeIds += dis.getId() + "," + dishame + (i == iup.size() - 1 ? "" : ";");//ID&工作类型【格式：12,打扫卫生;2,擦桌子;】
                        }
                    }
                }
                //查询缓存 缓存中不存在 查询数据库
                Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYWORKER + laf.getUserId());
                if (kitchenMap == null || kitchenMap.size() <= 0) {
                    HourlyWorker kitchen2 = hourlyWorkerService.findByUserId(laf.getUserId());
                    if (kitchen2 == null) {
                        return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,小时工不存在！", new JSONObject());
                    }
                    //放入缓存
                    kitchenMap = CommonUtils.objectToMap(kitchen2);
                    redisUtils.hmset(Constants.REDIS_KEY_HOURLYWORKER + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
                }
                HourlyWorker kh = (HourlyWorker) CommonUtils.mapToObject(kitchenMap, HourlyWorker.class);
                ShippingAddress s = shippingAddressService.findUserById(hourlyWorkerOrders.getAddressId());
                if (kh != null && s != null) {
                    long time = new Date().getTime();
                    String noTime = String.valueOf(time);
                    String random = CommonUtils.getRandom(6, 1);
                    String noRandom = CommonUtils.strToMD5(noTime + hourlyWorkerOrders.getMyId() + random, 16);

                    hourlyWorkerOrders.setNo(noRandom);//订单编号【MD5】
                    hourlyWorkerOrders.setAddTime(date);
                    hourlyWorkerOrders.setShopId(kh.getId());
                    hourlyWorkerOrders.setWorkerTypeIds(typeIds);
                    hourlyWorkerOrders.setAddressId(s.getId());
                    hourlyWorkerOrders.setMoney(0.00);//总价
                    hourlyWorkerOrders.setUserId(kh.getUserId());
                    hourlyWorkerOrders.setAddress(s.getAddress());
                    hourlyWorkerOrders.setAddress_city(s.getCity());
                    hourlyWorkerOrders.setAddress_district(s.getDistrict());
                    hourlyWorkerOrders.setAddress_province(s.getProvince());
                    hourlyWorkerOrders.setCoverMap(kh.getCoverCover());
                    hourlyWorkerOrders.setName(kh.getName());
                    hourlyWorkerOrders.setAddress_Name(s.getContactsName());
                    hourlyWorkerOrders.setAddress_Phone(s.getContactsPhone());
                    hourlyWorkerOrders.setAddress_postalcode(s.getPostalcode());

                    hourlyWorkerOrdersService.addOrders(hourlyWorkerOrders);

                    map.put("infoId", hourlyWorkerOrders.getNo());

                    //放入缓存
                    // 付款超时 15分钟
                    Map<String, Object> ordersMap = CommonUtils.objectToMap(hourlyWorkerOrders);
                    redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + hourlyWorkerOrders.getMyId() + "_" + hourlyWorkerOrders.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_15);
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
    public ReturnData delHourlyOrders(@PathVariable long id) {
        HourlyWorkerOrders io = hourlyWorkerOrdersService.findById(id, CommonUtils.getMyId(), 0);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersState() != 0) {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? (io.getOrdersState() == 1 ? 3 : 2) : (io.getOrdersState() == 2 ? 3 : 1));
        } else {
            io.setOrdersState(io.getUserId() == CommonUtils.getMyId() ? 2 : 1);
        }
        io.setUpdateCategory(0);
        hourlyWorkerOrdersService.updateOrders(io);
        //清除缓存中的小时工订单信息
        redisUtils.expire(Constants.REDIS_KEY_HOURLYORDERS + io.getMyId() + "_" + io.getNo(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由未接单改为已接单
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData hourlyReceipt(@PathVariable long id) {
        HourlyWorkerOrders io = hourlyWorkerOrdersService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由未接单改为制作中【已接单】
        if (io.getUserId() == CommonUtils.getMyId()) {
            io.setOrdersType(1);        //已接单
            io.setOrderTime(new Date());
            io.setUpdateCategory(1);
            hourlyWorkerOrdersService.updateOrders(io);
            //清除缓存中的小时工订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOURLYORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //厨房订单放入缓存(暂定30分钟接单超时)
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_15 * 2);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改单子状态
     * 由服务中改为已完成
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData hourlyComplete(@PathVariable long id) {
        HourlyWorkerOrders io = hourlyWorkerOrdersService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由服务中改为已完成
        if (io.getMyId() == CommonUtils.getMyId()) {
            io.setOrdersType(2);        //已完成
            io.setReceivingTime(new Date());
            io.setUpdateCategory(2);
            hourlyWorkerOrdersService.updateOrders(io);

            HourlyWorker kh = hourlyWorkerService.findByUserId(io.getUserId());
            kh.setTotalSales(kh.getTotalSales() + 1);
            hourlyWorkerService.updateNumber(kh);//更新小时工总服务次数
            //清除缓存中小时工的信息
            redisUtils.expire(Constants.REDIS_KEY_HOURLYWORKER + kh.getUserId(), 0);
            //清除缓存中的小时工订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOURLYORDERS + io.getNo(), 0);
            //小时工订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + io.getNo(), ordersMap, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData findHourlyOrders(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        HourlyWorkerOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = hourlyWorkerOrdersService.findByNo(no);
            if (io == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            UserInfo userInfo = null;
            userInfo = userInfoUtils.getUserInfo(io.getUserId() == CommonUtils.getMyId() ? io.getUserId() : io.getMyId());
            if (userInfo != null) {
                if (io.getUserId() == CommonUtils.getMyId()) {//卖家查看返回买家缓存信息  买家查看返回卖家实名信息
                    io.setName(userInfo.getName());
                    io.setCoverMap(userInfo.getHead());
                }
                io.setProTypeId(userInfo.getProType());
                io.setHouseNumber(userInfo.getHouseNumber());
            }
            //放入缓存
            ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData findHourlyNum(@PathVariable int identity) {
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;
        int orderCont4 = 0;
        int orderCont5 = 0;
        int orderCont6 = 0;
        int orderCont7 = 0;

        HourlyWorkerOrders kh = null;
        List list = null;
        list = hourlyWorkerOrdersService.findIdentity(identity, CommonUtils.getMyId());//全部
        for (int i = 0; i < list.size(); i++) {
            kh = (HourlyWorkerOrders) list.get(i);
            switch (kh.getOrdersType()) {//订单类型:  0已下单未付款  1已接单未完成  ,2已完成(已完成未评价),  3付款超时 4接单超时  5商家取消订单 6用户取消订单  7已评价
                case 0://已下单未付款
                    orderCont1++;
                    orderCont0++;//全部
                    break;
                case 1://已接单未完成
                    orderCont2++;
                    orderCont0++;
                    break;
                case 2://已完成(已完成未评价)
                    orderCont3++;
                    orderCont0++;
                    break;
                case 3://接单超时订单
                    orderCont4++;
                    orderCont0++;
                    break;
                case 4://卖家取消订单
                    orderCont5++;
                    orderCont0++;
                    break;
                case 5://买家取消订单
                    orderCont6++;
                    orderCont0++;
                    break;
                case 6://已评价
                    orderCont7++;
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
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @Override
    public ReturnData updHourlyOrdersType(@PathVariable long id) {
        HourlyWorkerOrders ko = null;
        ko = hourlyWorkerOrdersService.findById(id, CommonUtils.getMyId(), 3);
        if (ko == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        //商家取消订单
        if (ko.getUserId() == CommonUtils.getMyId()) {
            if (ko.getOrdersType() == 1) {//商家可以取消已接单并且未完成的单子
                ko.setOrdersType(4);
            }
        }
        if (ko.getMyId() == CommonUtils.getMyId()) {//用户可以取消商家未接单状态的单子
            if (ko.getOrdersType() == 0) {
                ko.setOrdersType(5);
            }
        }
        ko.setUpdateCategory(3);
        hourlyWorkerOrdersService.updateOrders(ko);//更新订单
        if (ko.getOrdersType() == 4 || ko.getOrdersType() == 5) {
            //更新缓存、钱包、账单
            mqUtils.sendPurseMQ(ko.getMyId(), 16, 0, ko.getMoney());
            //清除缓存中的小时工订单信息
            redisUtils.expire(Constants.REDIS_KEY_HOURLYORDERS + ko.getMyId() + "_" + ko.getNo(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
            redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + ko.getMyId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增评价点赞
     * @param ev
     * @return
     */
    @Override
    public ReturnData addHourlyEvaluate(@Valid @RequestBody HourlyWorkerEvaluate ev, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        HourlyWorkerType dis = null;
        List list = null;
        HourlyWorkerOrders io = hourlyWorkerOrdersService.findById(ev.getOrderId(), CommonUtils.getMyId(), 4);
        if (io != null && io.getOrdersType() == 2) {
            //查询缓存 缓存中不存在 查询数据库(小时工)
            Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYWORKER + io.getUserId());
            if (kitchenMap == null || kitchenMap.size() <= 0) {
                HourlyWorker kitchen = hourlyWorkerService.findByUserId(io.getUserId());
                if (kitchen == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,小时工不存在", new JSONObject());
                }
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen);
                redisUtils.hmset(Constants.REDIS_KEY_HOURLYWORKER + kitchen.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
            HourlyWorker kh = (HourlyWorker) CommonUtils.mapToObject(kitchenMap, HourlyWorker.class);
            if (kh != null) {
                if (!CommonUtils.checkFull(ev.getTypeIds())) {
                    String[] sd = ev.getTypeIds().split(",");//工种ID
                    list = hourlyWorkerService.findDishesList(sd);
                    if (list != null && list.size() > 0) {
                        String[] workerTypeIds = io.getWorkerTypeIds().split(";");
                        if (list.size() == sd.length && workerTypeIds != null) {
                            for (int i = 0; i < list.size(); i++) {
                                dis = (HourlyWorkerType) list.get(i);
                                for (int j = 0; j < workerTypeIds.length; j++) {
                                    String[] types = workerTypeIds[j].split(",");
                                    long workeId = Long.parseLong(types[0]);
                                    if (workeId == dis.getId()) {
                                        HourlyWorkerFabulous like = new HourlyWorkerFabulous();
                                        like.setMyId(CommonUtils.getMyId());
                                        like.setUserId(dis.getUserId());
                                        like.setTypeId(dis.getId());
                                        like.setTime(new Date());
                                        like.setStatus(0);    //0正常
                                        hourlyWorkerOrdersService.addLike(like);

                                        //更新工种点赞数
                                        dis.setPointNumber(dis.getPointNumber() + 1);
                                        hourlyWorkerService.updateLike(dis);
                                    }
                                }
                            }
                        }
                    }
                }
                ev.setWorkerId(io.getShopId());
                ev.setOrderId(io.getId());
                ev.setCover(kh.getCoverCover());
                ev.setTime(new Date());

                hourlyWorkerOrdersService.addEvaluate(ev);

                kh.setTotalScore(ev.getScore() + kh.getTotalScore());
                hourlyWorkerService.updateScore(kh);//更新小时工总评分

                io.setOrdersType(6);//更新订单状态为已评价
                io.setUpdateCategory(4);
                hourlyWorkerOrdersService.updateOrders(io);
                //清除缓存中的厨房订单信息
                redisUtils.expire(Constants.REDIS_KEY_HOURLYORDERS + io.getMyId() + "_" + io.getNo(), 0);
                //放入缓存
                Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
                redisUtils.hmset(Constants.REDIS_KEY_HOURLYORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  0已下单未付款  1已接单未完成  ,2已完成(已完成未评价),  3接单超时  4商家取消订单 5用户取消订单  6已评价
     * @return
     */
    @Override
    public ReturnData findHourlyOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HourlyWorkerOrders> pageBean;
        pageBean = hourlyWorkerOrdersService.findOrderList(identity, userId, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        HourlyWorkerOrders t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (HourlyWorkerOrders) list.get(i);
                if (t != null) {
                    if (userCache != null) {
                        if (identity == 1) {
                            userCache = userInfoUtils.getUserInfo(t.getUserId());
                        } else {
                            userCache = userInfoUtils.getUserInfo(t.getMyId());
                            t.setName(userCache.getName());
                            t.setCoverMap(userCache.getHead());
                        }
                        t.setProTypeId(userCache.getProType());
                        t.setHouseNumber(userCache.getHouseNumber());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }
}
