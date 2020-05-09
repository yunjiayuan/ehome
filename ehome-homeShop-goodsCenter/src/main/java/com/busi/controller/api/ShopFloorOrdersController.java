package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.PartnerBuyService;
import com.busi.service.ShopFloorGoodsService;
import com.busi.service.ShopFloorOrdersService;
import com.busi.service.ShopFloorShoppingCartService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 楼店订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2019-12-17 15:13
 */
@RestController
public class ShopFloorOrdersController extends BaseController implements ShopFloorOrdersApiController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    ShippingAddressUtils addressUtils;

    @Autowired
    ShopFloorGoodsService shopFloorGoodsService;

    @Autowired
    ShopFloorOrdersService shopFloorOrdersService;

    @Autowired
    private ShopFloorShoppingCartService goodsCenterService;

    /***
     * 新增订单
     * @param shopFloorOrders
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addSForders(@Valid @RequestBody ShopFloorOrders shopFloorOrders, BindingResult bindingResult) {
        Map<String, Object> map = new HashMap<>();
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        ShippingAddress shippingAddress = null;
        if (shopFloorOrders.getType() == 0) {//普通订单下单时需要地址
            shippingAddress = addressUtils.findAddress(shopFloorOrders.getAddressId());
            if (shippingAddress == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "下单失败！收货地址不存在！", new JSONObject());
            }
            shopFloorOrders.setAddress(shippingAddress.getAddress());
            shopFloorOrders.setAddressName(shippingAddress.getContactsName());
            shopFloorOrders.setAddressPhone(shippingAddress.getContactsPhone());
            shopFloorOrders.setAddressCity(shippingAddress.getCity());
            shopFloorOrders.setAddressDistrict(shippingAddress.getDistrict());
            shopFloorOrders.setAddressProvince(shippingAddress.getProvince());
        }
        String goods = ""; //商品信息
        String goodsTitle = ""; //商品标题
        String basicDescribe = "";  //基本描述
        double cost = 0.00; //商品价格
        double money = 0.00; // 商品总金额
        String imgUrl = "";   //图片
        String specs = "";    //规格
        List iup = null;
        ShopFloorGoods laf = null;
        if (CommonUtils.checkFull(shopFloorOrders.getGoodsIds()) || CommonUtils.checkFull(shopFloorOrders.getGoodsNumber())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] sd = shopFloorOrders.getGoodsIds().split(",");//商品ID
        String[] fn = shopFloorOrders.getGoodsNumber().split(",");//商品数量
        if (shopFloorOrders.getType() < 3) { //订单类型：0普通  1礼尚往来指定接收者  2礼尚往来未指定接收者  3合伙购
            iup = shopFloorGoodsService.findList(sd);
            if (iup == null || iup.size() <= 0 || iup.size() != sd.length) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            for (int i = 0; i < iup.size(); i++) {
                laf = (ShopFloorGoods) iup.get(i);
                for (int j = 0; j < sd.length; j++) {
                    if (laf.getId() == Long.parseLong(sd[j])) {//确认是当前商品ID
                        //判断是否有折扣
                        if (laf.getDiscountPrice() > 0) {
                            cost = laf.getDiscountPrice();//折扣价
                        } else {
                            cost = laf.getPrice();//原价
                        }
                        goodsTitle = laf.getGoodsTitle();//标题
                        if (!CommonUtils.checkFull(laf.getBasicDescribe())) {
                            basicDescribe = laf.getBasicDescribe(); //基本描述
                        }
                        if (CommonUtils.checkFull(laf.getGoodsCoverUrl())) {
                            String[] img = laf.getImgUrl().split(",");
                            imgUrl = img[0];//用第一张图做封面
                        } else {
                            imgUrl = laf.getGoodsCoverUrl();//图片
                        }
                        specs = laf.getSpecs();
                        goods += laf.getId() + "," + goodsTitle + "," + Integer.parseInt(fn[j]) + "," + cost + "," + imgUrl + "," + specs + "," + basicDescribe + (i == iup.size() - 1 ? "" : ";");//商品ID,标题,数量,价格，图片,规格,基本描述;
                        money += Integer.parseInt(fn[j]) * cost;//总价格
                    }
                }
            }
            //移除购物车当前商品
            goodsCenterService.delGoods(sd);
            //清除缓存中购物车的信息
            redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_CARTLIST + shopFloorOrders.getBuyerId(), 0);
        } else {//订单类型: 3合伙购
            PartnerBuyGoods buyGoods = null;
            buyGoods = shopFloorGoodsService.find(Long.parseLong(sd[0]));
            if (buyGoods == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "当前合伙购不存在", new JSONObject());
            }
            if (buyGoods.getState() == 1 || buyGoods.getNumber() >= buyGoods.getLimitNumber()) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "当前合伙购已拼购成功，请看看其他的吧", new JSONObject());
            }
            String usr = "#" + shopFloorOrders.getBuyerId() + "#";
            String[] personnel = buyGoods.getPersonnel().split(";");
            for (int i = 0; i < personnel.length; i++) {
                String[] personne = personnel[i].split(",");
                if (usr.equals(personne[0])) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "你已加入过此合伙购了", new JSONObject());
                }
            }
            if (buyGoods.getId() == Long.parseLong(sd[0])) {//确认是当前商品ID
                //判断是否有合伙购价格
                if (buyGoods.getPartnerPrice() > 0) {
                    cost = buyGoods.getPartnerPrice();//合伙购价格
                } else {
                    cost = buyGoods.getPrice();//原价
                }
                goodsTitle = buyGoods.getGoodsTitle();//标题
                if (!CommonUtils.checkFull(buyGoods.getImgUrl())) {
                    String[] img = buyGoods.getImgUrl().split(",");
                    imgUrl = img[0];//用第一张图做封面
                }
                specs = buyGoods.getSpecs();
                goods = buyGoods.getId() + "," + goodsTitle + "," + Integer.parseInt(fn[0]) + "," + cost + "," + imgUrl + "," + specs;//商品ID,标题,数量,价格，图片,规格;
                money = Integer.parseInt(fn[0]) * cost;//总价格
            }
        }
        Date date = new Date();
        long time = new Date().getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + shopFloorOrders.getBuyerId() + random, 16);

        shopFloorOrders.setNo(noRandom);//订单编号【MD5】
        shopFloorOrders.setAddTime(date);
        shopFloorOrders.setGoods(goods);//商品ID,标题,数量,价格，图片,规格;
        shopFloorOrders.setMoney(money);//总价
        shopFloorOrders.setOrdersType(0);
        shopFloorOrdersService.addOrders(shopFloorOrders);

        map.put("no", shopFloorOrders.getNo());
        //放入缓存
        // 付款超时 45分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(shopFloorOrders);
        redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + shopFloorOrders.getBuyerId() + "_" + shopFloorOrders.getNo(), ordersMap, Constants.TIME_OUT_MINUTE_45);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新礼尚往来订单
     * @param shopFloorOrders
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData upSFreceiveState(@Valid @RequestBody ShopFloorOrders shopFloorOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        ShippingAddress shippingAddress = null;
        shippingAddress = addressUtils.findAddress(shopFloorOrders.getAddressId());
        if (shippingAddress == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "领取失败！收货地址不存在！", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        ShopFloorOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOORORDERS + shopFloorOrders.getBuyerId() + "_" + shopFloorOrders.getNo());
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = shopFloorOrdersService.findNo(shopFloorOrders.getNo());
            if (io == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            if (io.getReceiveState() != 0) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您已领取过了", new JSONObject());
            }
            if (shopFloorOrders.getRecipientId() > 0) {
                io.setRecipientId(shopFloorOrders.getRecipientId());
            }
            if (shopFloorOrders.getShopId() > 0) {
                io.setShopId(shopFloorOrders.getShopId());
                io.setShopName(shopFloorOrders.getShopName());
            }
            io.setReceiveState(1);
            io.setOrdersType(1);
            io.setAddress(shippingAddress.getAddress());
            io.setAddressName(shippingAddress.getContactsName());
            io.setAddressPhone(shippingAddress.getContactsPhone());
            io.setAddressCity(shippingAddress.getCity());
            io.setAddressDistrict(shippingAddress.getDistrict());
            io.setAddressProvince(shippingAddress.getProvince());
            shopFloorOrdersService.upSFreceiveState(io);
            //放入缓存
            ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        ShopFloorOrders ik = (ShopFloorOrders) CommonUtils.mapToObject(ordersMap, ShopFloorOrders.class);
        if (ik == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        if (ik.getReceiveState() != 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您已领取过了", new JSONObject());
        }
        if (shopFloorOrders.getRecipientId() > 0) {
            io.setRecipientId(shopFloorOrders.getRecipientId());
        }
        if (shopFloorOrders.getShopId() > 0) {
            io.setShopId(shopFloorOrders.getShopId());
            io.setShopName(shopFloorOrders.getShopName());
        }
        ik.setReceiveState(1);
        ik.setOrdersType(1);
        ik.setAddress(shippingAddress.getAddress());
        ik.setAddressName(shippingAddress.getContactsName());
        ik.setAddressPhone(shippingAddress.getContactsPhone());
        ik.setAddressCity(shippingAddress.getCity());
        ik.setAddressDistrict(shippingAddress.getDistrict());
        ik.setAddressProvince(shippingAddress.getProvince());
        shopFloorOrdersService.upSFreceiveState(ik);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + ik.getBuyerId() + "_" + ik.getNo(), 0);
        //放入缓存
        ordersMap = CommonUtils.objectToMap(ik);
        redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + ik.getBuyerId() + "_" + ik.getNo(), ordersMap, Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delSForders(@PathVariable long id) {
        ShopFloorOrders io = shopFloorOrdersService.findById(id, CommonUtils.getMyId(), 0);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        io.setUpdateCategory(0);
        shopFloorOrdersService.updateOrders(io);
        //清除缓存中的厨房订座订单信息
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改订单状态
     * 由待送出改为待发货（已送出）
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData changeSFsendOut(@PathVariable long id) {
        ShopFloorOrders io = shopFloorOrdersService.findById(id, CommonUtils.getMyId(), 4);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getBuyerId() == CommonUtils.getMyId()) {
            io.setOrdersType(1);
            io.setUpdateCategory(3);
            shopFloorOrdersService.updateOrders(io);

            //清除缓存中的订单信息
            redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), 0);
            //订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改发货状态
     * 由未发货改为已发货（由已送出改为已发货）
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData changeSFdeliver(@PathVariable long id) {
        ShopFloorOrders io = shopFloorOrdersService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getBuyerId() != CommonUtils.getMyId()) {
            io.setOrdersType(2);
            io.setDeliveryTime(new Date());
            io.setUpdateCategory(1);
            shopFloorOrdersService.updateOrders(io);

            //清除缓存中的订单信息
            redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), 0);
            //订单放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更改收货状态
     * 由未收货改为已收货
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData changeSFreceipt(@PathVariable long id) {
        ShopFloorOrders io = shopFloorOrdersService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getType() == 0) {
            if (io.getBuyerId() != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
        }
        if (io.getType() == 1 || io.getType() == 2) {  //订单类型：0普通  1礼尚往来指定接收者  2礼尚往来未指定接收者  3合伙购
            if (io.getRecipientId() != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
        }
        //由已接单改为已完成
        io.setOrdersType(3);        //已收货
        io.setReceivingTime(new Date());
        io.setUpdateCategory(2);
        shopFloorOrdersService.updateOrders(io);
        //更新销量

        //清除缓存中的商品信息

        //清除缓存中的订单信息
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), 0);
        //订单放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + io.getNo(), ordersMap, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询订单列表
     * @param type    0黑店订单  1礼尚往来
     * @param ordersType 订单类型: -1全部 0待付款,1待发货(已付款),2已发货（待收货）, 3已收货（待评价）  4已评价  5付款超时、发货超时、取消订单  8待送出（礼尚往来） 9待领取
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findSFordersList(@PathVariable int type, @PathVariable int ordersType,
                                       @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ShopFloorOrders> pageBean;
        pageBean = shopFloorOrdersService.findOrderList(type, CommonUtils.getMyId(), ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        ShopFloorOrders t = null;
        UserInfo userCache = null;
        UserInfo userCache2 = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (ShopFloorOrders) list.get(i);
                if (t != null) {
                    if (ordersType == -1 || ordersType == 9) {
                        if (t.getOrdersType() == 1 || t.getOrdersType() == 8) {
                            if (t.getReceiveState() == 0) {
                                t.setOrdersType(9);
                            }
                        }
                    }
                    userCache = userInfoUtils.getUserInfo(t.getBuyerId());
                    if (userCache != null) {
                        t.setName(userCache.getName());
                        t.setHead(userCache.getHead());
                        t.setProTypeId(userCache.getProType());
                        t.setHouseNumber(userCache.getHouseNumber());
                    }
                    if (t.getRecipientId() > 0) {
                        userCache2 = userInfoUtils.getUserInfo(t.getRecipientId());
                        if (userCache != null) {
                            t.setRecipientName(userCache2.getName());
                            t.setRecipientHead(userCache2.getHead());
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

    /***
     * 取消订单
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData cancelSForders(@PathVariable long id) {
        ShopFloorOrders ko = null;
        ko = shopFloorOrdersService.findById(id, CommonUtils.getMyId(), 3);
        if (ko == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        ko.setOrdersType(7);
        ko.setUpdateCategory(3);
        shopFloorOrdersService.updateOrders(ko);//更新订单
        //更新缓存、钱包、账单
        if (ko.getOrdersType() > 0) {
            mqUtils.sendPurseMQ(ko.getBuyerId(), 27, 0, ko.getMoney());
        }
        //清除缓存中的订单信息
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOORORDERS + ko.getBuyerId() + "_" + ko.getNo(), 0);
        //放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
        redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + ko.getBuyerId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData orderDetails(@PathVariable String no) {
        // 查询数据库
        ShopFloorOrders io = null;
        io = shopFloorOrdersService.findNo(no);
        if (io == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(io.getBuyerId());
        if (userInfo != null) {
            io.setName(userInfo.getName());
            io.setHead(userInfo.getHead());
            io.setProTypeId(userInfo.getProType());
            io.setHouseNumber(userInfo.getHouseNumber());
        }
        //放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 统计各类订单数量
     * @return
     */
    @Override
    public ReturnData findSFordersCount() {
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;
        int orderCont4 = 0;
        int orderCont5 = 0;
        int orderCont6 = 0;
        int orderCont7 = 0;

        Map<String, Object> map = new HashMap<>();
        map.put("orderCont0", orderCont0);
        map.put("orderCont1", orderCont1);
        map.put("orderCont2", orderCont2);
        map.put("orderCont3", orderCont3);
        map.put("orderCont4", orderCont4);
        map.put("orderCont5", orderCont5);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("orderCont0", orderCont0);
        map2.put("orderCont1", orderCont1);
        map2.put("orderCont2", orderCont2);
        map2.put("orderCont3", orderCont3);
        map2.put("orderCont4", orderCont4);
        map2.put("orderCont5", orderCont5);
        map2.put("orderCont6", orderCont6);
        map2.put("orderCont7", orderCont7);
        List list = null;
        ShopFloorOrders kh = null;
        List<Map<String, Object>> newList = new ArrayList<>();//最终组合后List
        list = shopFloorOrdersService.findIdentity(CommonUtils.getMyId());//全部
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", newList);
        }
        for (int i = 0; i < list.size(); i++) {
            kh = (ShopFloorOrders) list.get(i);
            if (kh.getType() == 0) {
                switch (kh.getOrdersType()) {
                    case 0://待付款
                        orderCont1++;
                        orderCont0++;
                        break;
                    case 1://待发货
                        orderCont2++;
                        orderCont0++;
                        break;
                    case 2://待收货
                        orderCont3++;
                        orderCont0++;
                        break;
                    case 3://待评价
                        orderCont4++;
                        orderCont0++;
                        break;
                    case 7://取消订单
                        orderCont5++;
                        orderCont0++;
                        break;
                    default:
                        orderCont0++;
                        break;
                }
                map.put("orderCont0", orderCont0);
                map.put("orderCont1", orderCont1);
                map.put("orderCont2", orderCont2);
                map.put("orderCont3", orderCont3);
                map.put("orderCont4", orderCont4);
                map.put("orderCont5", orderCont5);
            } else {
                if (CommonUtils.getMyId() == kh.getBuyerId()) {
                    if (kh.getOrdersType() == 0) {//待付款
                        orderCont1++;
                    } else if (kh.getOrdersType() == 1) {//待发货
                        orderCont2++;
                    } else if (kh.getOrdersType() == 2) {//待收货
                        orderCont3++;
                    } else if (kh.getOrdersType() == 3) {//待评价
                        orderCont4++;
                    } else if (kh.getOrdersType() == 8) {//待送出
                        orderCont6++;
                    } else if (kh.getOrdersType() == 7) {//取消订单
                        orderCont5++;
                    }
                } else if (kh.getReceiveState() == 0 && CommonUtils.getMyId() == kh.getRecipientId()) {//待领取
                    if (kh.getOrdersType() == 1 || kh.getOrdersType() == 8) {
                        orderCont7++;
                    }
                }
                orderCont0++;
                map2.put("orderCont0", orderCont0);
                map2.put("orderCont1", orderCont1);
                map2.put("orderCont2", orderCont2);
                map2.put("orderCont3", orderCont3);
                map2.put("orderCont4", orderCont4);
                map2.put("orderCont5", orderCont5);
                map2.put("orderCont6", orderCont6);
                map2.put("orderCont7", orderCont7);
            }
        }
        newList.add(map);
        newList.add(map2);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", newList);
    }
}
