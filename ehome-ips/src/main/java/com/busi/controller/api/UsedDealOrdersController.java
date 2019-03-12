package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.UsedDealOrdersService;
import com.busi.service.UsedDealService;
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
 * @description: 二手订单
 * @author: ZHaoJiaJie
 * @create: 2018-10-25 14:35
 */
@RestController
public class UsedDealOrdersController extends BaseController implements UsedDealOrdersApiController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    LogisticsUtils logisticsUtils;

    @Autowired
    ShippingAddressUtils addressUtils;

    @Autowired
    UsedDealService usedDealService;

    @Autowired
    UsedDealOrdersService usedDealOrdersService;

    /***
     * 新增二手订单
     * @param usedDealOrders
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addOrders(@Valid @RequestBody UsedDealOrders usedDealOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        UsedDeal usedDeal = usedDealService.findUserById(usedDealOrders.getGoodsId());
        ShippingAddress shippingAddress = addressUtils.findAddress(usedDealOrders.getAddressId());
        if (usedDeal == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "下单失败！商品不存在！", new JSONObject());
        }
        if (usedDeal.getUserId() == CommonUtils.getMyId()) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "下单失败！不能购买自己的商品！", new JSONObject());
        }
        if (shippingAddress == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "下单失败！收货地址不存在！", new JSONObject());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        long time1 = new Date().getTime();
        String noTime = String.valueOf(time1);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + CommonUtils.getMyId() + random, 16);

        //更改商品状态为已卖出
        usedDeal.setSellType(3);
        usedDealOrders.setAddTime(new Date());
        usedDealOrders.setOrderNumber(noRandom);//订单编号【MD5】
        usedDealOrders.setTitle(usedDeal.getTitle());
        usedDealOrders.setUserId(usedDeal.getUserId());
        usedDealOrders.setPicture(usedDeal.getImgUrl());
        usedDealOrders.setAfficheType(2);
        usedDealOrders.setSellingPrice(usedDeal.getSellingPrice());
        usedDealOrders.setPinkageType(usedDeal.getPinkageType());//是否包邮
        usedDealOrders.setAddressId(shippingAddress.getId());
        usedDealOrders.setAddress(shippingAddress.getAddress());
        usedDealOrders.setAddress_city(shippingAddress.getCity());
        usedDealOrders.setAddress_district(shippingAddress.getDistrict());
        usedDealOrders.setAddress_province(shippingAddress.getProvince());
        usedDealOrders.setAddress_Name(shippingAddress.getContactsName());
        usedDealOrders.setAddress_Phone(shippingAddress.getContactsPhone());
        usedDealOrders.setAddress_postalcode(shippingAddress.getPostalcode());
        String dm = usedDeal.getExpressMode();
        if (usedDeal.getPinkageType() == 1) {//不包邮
            if (usedDealOrders.getDistributioMode() == -1) {//到付
                usedDealOrders.setPostage(0);
                usedDealOrders.setDistributioMode(-1);
            } else {
                if (!CommonUtils.checkFull(dm)) {
                    String[] dmbArrey = dm.split(";");
                    if (dmbArrey != null && dmbArrey.length > usedDealOrders.getDistributioMode()) {
                        String[] dmArrey = dmbArrey[usedDealOrders.getDistributioMode()].split(",");
                        if (dmArrey != null && dmArrey.length == 2) {
                            usedDealOrders.setDistributioMode(Integer.parseInt(dmArrey[0]));
                            usedDealOrders.setPostage(Integer.parseInt(dmArrey[1]));
                        }
                    }
                }
            }
        }
        usedDealOrders.setMoney(usedDeal.getSellingPrice() + usedDealOrders.getPostage());
        //新增物流信息
        UsedDealLogistics ls = new UsedDealLogistics();
        ls.setUserId(usedDealOrders.getUserId());
        ls.setMyId(usedDealOrders.getMyId());
        ls.setData(time + "&&系统已接单,等待付款");
        usedDealOrdersService.addLogistics(ls);

        usedDealOrders.setLogisticsId(ls.getId());//物流ID
        usedDealOrdersService.addOrders(usedDealOrders);
        usedDealService.updateStatus(usedDeal);

        //更新home
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        for (int i = 0; i < list.size(); i++) {
            IPS_Home home = (IPS_Home) list.get(i);
            if (home.getAfficheType() == 2 && home.getInfoId() == usedDeal.getId()) {
                redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
            }
        }
        //清除缓存中的二手信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEAL + usedDeal.getId(), 0);

        //放入缓存
        // 付款超时 45分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(usedDealOrders);
        redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEALORDERS + noRandom, ordersMap, Constants.TIME_OUT_MINUTE_45);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", usedDealOrders.getOrderNumber());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delOrders(@PathVariable long id) {
        UsedDealOrders io = usedDealOrdersService.findDelOrId(id);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getOrdersState() != 0) {
            if (io.getUserId() == CommonUtils.getMyId()) {//商家删除操作
                if (io.getOrdersState() == 1) {
                    io.setOrdersState(3);
                } else {
                    io.setOrdersState(2);
                }
            } else {//买家删除操作
                if (io.getOrdersState() == 2) {
                    io.setOrdersState(3);
                } else {
                    io.setOrdersState(1);
                }
            }
        } else {
            if (io.getUserId() == CommonUtils.getMyId()) {//商家删除操作
                io.setOrdersState(2);
            } else {
                io.setOrdersState(1);
            }
        }
        usedDealOrdersService.delOrders(io);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改发货状态
     * 由未发货改为已发货
     * @param infoId  订单Id
     * @param no  订单编号
     * @param brand  订单物流方式下标
     * @return
     */
    @Override
    public ReturnData changeDeliverGoods(@PathVariable long infoId, @PathVariable String no, @PathVariable int brand) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        UsedDealOrders io = usedDealOrdersService.findDeliverOrId(infoId);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getPaymentTime() != null) {
            long payTime = new Date().getTime() - io.getPaymentTime().getTime();
            if (payTime > 3 * 24 * 60 * 60 * 1000 - 30 * 60 * 1000) {//30分钟时间差 给定时任务
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "该订单已超时！", new JSONObject());
            }
        }
        io.setOrdersType(2);        //待收货
        io.setDeliveryTime(new Date());
        io.setDelayTime(io.getDeliveryTime());
        UsedDealLogistics ls = usedDealOrdersService.findLogistics(io.getLogisticsId());
        if (ls != null) {
            //更新物流信息
            ls.setNo(no);
            ls.setBrand(brand);
            ls.setData(time + "&&卖家已发货##" + ls.getData());
            usedDealOrdersService.updateLogistics(ls);
        }
        usedDealOrdersService.updateDelivery(io);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), 0);
        //放入缓存
        // 收货超时 两周
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_60_24_1 * 14);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改收货状态
     * 由未收货改为已收货
     * @param infoId  订单Id
     * @return
     */
    @Override
    public ReturnData changeGoodsReceipt(@PathVariable long infoId) {
        UsedDealOrders io = usedDealOrdersService.findReceiptOrId(infoId);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        io.setOrdersType(3);        //待评价
        io.setReceivingTime(new Date());

        usedDealOrdersService.updateCollect(io);

        //支付商家
        mqUtils.sendPurseMQ(io.getUserId(), 14, 0, io.getMoney());

        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), 0);
        //放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), ordersMap, 0);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询二手订单列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity  身份区分：1买家 2商家
     * @param ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时
     * @return
     */
    @Override
    public ReturnData findOrdersList(@PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<UsedDealOrders> pageBean;
        pageBean = usedDealOrdersService.findOrderList(identity, CommonUtils.getMyId(), ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        UsedDealOrders t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (UsedDealOrders) list.get(i);
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

    /***
     * 延长收货时间
     * @param infoId  订单Id
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData timeExpand(@PathVariable long infoId, @PathVariable int identity) {
        UsedDealOrders io = usedDealOrdersService.findReceiptOrId(infoId);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (identity == 1) {
            if (io.getMyId() == CommonUtils.getMyId()) {//买家
                if (io.getExtendFrequency() < 1) {//买家延长次数只有一次（一次三天）
                    //延长三天后时间
                    io.setDelayTime(new Date(io.getDeliveryTime().getTime() + 86400000 * 3));//发货时间+3天【作为新的发货时间】
                } else {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "买家只能延长一次！", new JSONObject());
                }
            }
        } else {//商家[无限制延长]
            if (io.getUserId() == CommonUtils.getMyId()) {//商家
                if (io.getExtendFrequency() < 1) {
                    io.setDelayTime(new Date(io.getDeliveryTime().getTime() + 86400000 * 3));//发货时间+3天【作为新的发货时间】
                } else {
                    io.setDelayTime(new Date(io.getDelayTime().getTime() + 86400000 * 3));//延时后发货时间【作为新的发货时间】
                }
            }
        }
        io.setExtendFrequency(io.getExtendFrequency() + 1);//延长次数
        usedDealOrdersService.timeExpand(io);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), 0);
        //放入缓存
        //收货超时 两周
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_60_24_1 * 14);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 取消订单
     * @param infoId  订单Id
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData cancelOrders(@PathVariable long infoId, @PathVariable int identity) {
        UsedDealOrders io = usedDealOrdersService.findCancelOrId(infoId);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        UsedDeal iup = usedDealService.findUserById(io.getGoodsId());
        if (iup != null) {
            if (identity == 1) {
                io.setOrdersType(4);
            } else {
                io.setOrdersType(5);
            }
            iup.setSellType(1);

            usedDealOrdersService.cancelOrders(io);//更新订单
            usedDealService.updateStatus(iup);//更新二手

            //清除缓存中的二手信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEAL + iup.getId(), 0);
            //清除缓存中的二手订单信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEALORDERS + io.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_60_24_1 * 7);
        } else {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "商品不存在！", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param infoId  订单Id
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData ordersDetails(@PathVariable String infoId, @PathVariable int identity) {
        //查询缓存 缓存中不存在 查询数据库
        UsedDealOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_USEDDEALORDERS + infoId);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = usedDealOrdersService.findDetailsOrId(infoId);
            if (io == null) {
                return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            UserInfo userInfo = null;
            if (identity == 1) {
                userInfo = userInfoUtils.getUserInfo(io.getUserId());
            } else {
                userInfo = userInfoUtils.getUserInfo(io.getMyId());
            }
            if (userInfo != null) {
                io.setName(userInfo.getName());
                io.setHead(userInfo.getHead());
                io.setProTypeId(userInfo.getProType());
                io.setHouseNumber(userInfo.getHouseNumber());
            }
            //放入缓存
            ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEALORDERS + infoId, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Override
    public ReturnData findBabyCount(@PathVariable int identity) {
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;
        int orderCont4 = 0;
        int orderCont5 = 0;
        int orderCont6 = 0;
        int orderCont7 = 0;
        int orderCont8 = 0;

        orderCont0 = usedDealOrdersService.findNum(identity, -1, CommonUtils.getMyId());//全部
        orderCont1 = usedDealOrdersService.findNum(identity, 0, CommonUtils.getMyId());//待付款(未付款)
        orderCont2 = usedDealOrdersService.findNum(identity, 1, CommonUtils.getMyId());//待发货(已付款未发货)
        orderCont3 = usedDealOrdersService.findNum(identity, 2, CommonUtils.getMyId());//待收货(已发货未收货)
        orderCont4 = usedDealOrdersService.findNum(identity, 3, CommonUtils.getMyId());//待评价(已收货未评价)
        orderCont5 = usedDealOrdersService.findNum(identity, 4, CommonUtils.getMyId());//用户取消订单
        orderCont6 = usedDealOrdersService.findNum(identity, 5, CommonUtils.getMyId());//卖家取消订单
        orderCont7 = usedDealOrdersService.findNum(identity, 6, CommonUtils.getMyId());//付款超时订单
        orderCont8 = usedDealOrdersService.findNum(identity, 7, CommonUtils.getMyId());//发货超时订单

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
     * 新增二手快递
     * @param usedDealExpress
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addExpress(@Valid @RequestBody UsedDealExpress usedDealExpress, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户快递个数是否达到上限  最多10条
        int num = usedDealOrdersService.findExpressNum(CommonUtils.getMyId());
        if (num > 10) {//超过上限
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "新增快递数量超过上限!", new JSONObject());
        }
        Date date = new Date();
        usedDealExpress.setAddTime(date);
        usedDealOrdersService.addExpress(usedDealExpress);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新二手快递
     * @Param: usedDealExpress
     * @return:
     */
    @Override
    public ReturnData updateExpress(@Valid @RequestBody UsedDealExpress usedDealExpress, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != usedDealExpress.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + usedDealExpress.getUserId() + "]的快递信息", new JSONObject());
        }
        // 查询数据库
        UsedDealExpress posts = usedDealOrdersService.findExpress(usedDealExpress.getId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "快递不存在！", new JSONObject());
        }
        usedDealOrdersService.updateExpress(usedDealExpress);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除快递
     * @param id 快递ID
     * @return
     */
    @Override
    public ReturnData delExpress(@PathVariable long id) {
        // 查询数据库
        UsedDealExpress posts = usedDealOrdersService.findExpress(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "快递不存在！", new JSONObject());
        }
        posts.setExpressSate(1);
        usedDealOrdersService.delExpress(posts);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询快递列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @return
     */
    @Override
    public ReturnData findExpress(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<UsedDealExpress> pageBean = null;
        pageBean = usedDealOrdersService.findExpressList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查看订单物流详情
     * @param infoId  物流Id
     * @return
     */
    @Override
    public ReturnData logisticsDetails(@PathVariable long infoId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 5); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒
        long time = calendar.getTimeInMillis();         // 此处为今天的05：00：00
        long curren = 3600000;//一小时毫秒数
        long da = new Date().getTime();//当前时间毫秒数
        // 查询数据库
        UsedDealLogistics io = usedDealOrdersService.logisticsDetails(infoId);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "物流不存在！", new JSONObject());
        }
        String brandName = Constants.expressModeArray[io.getBrand()].split(",")[1];
        //22-05点查数据库物流信息，其余时间按时间段区分查询渠道
        if (da >= time && da < time + curren * 17) {//5-22
            if ((da >= time && da < time + curren)//5-6
                    || (da >= time + curren * 2 && da < time + curren * 3)//7-8
                    || (da >= time + curren * 4 && da < time + curren * 5)//9-10
                    || (da >= time + curren * 6 && da < time + curren * 7)//11-12
                    || (da >= time + curren * 8 && da < time + curren * 9)//13-14
                    || (da >= time + curren * 10 && da < time + curren * 11)//15-16
                    || (da >= time + curren * 12 && da < time + curren * 13)//17-18
                    || (da >= time + curren * 14 && da < time + curren * 15)//19-20
                    || (da >= time + curren * 16 && da < time + curren * 17)) {//21-22
                //查询第三方物流信息
                String brand = Constants.expressModeArray[io.getBrand()].split(",")[0];
                UsedDealLogistics data = logisticsUtils.findLogisticsInfo(brand, io.getNo());
                if (data != null) {
                    if (!CommonUtils.checkFull(data.getData())) {
                        String lInfo = io.getData();
                        if (!CommonUtils.checkFull(lInfo)) {
                            String[] array = lInfo.split("##");
                            io.setData(data.getData() + "##" + array[0] + "##" + array[1] + "##" + array[2]);
                        }
                        usedDealOrdersService.updateLogisticsData(io);
                    }
                }
            }
        }
        io.setBrandName(brandName);//物流名称

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", io);
    }
}
