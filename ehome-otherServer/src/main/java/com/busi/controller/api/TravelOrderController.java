package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.TravelOrderService;
import com.busi.service.TravelService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 家门口旅游订单
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 13:45:37
 */
@RestController
public class TravelOrderController extends BaseController implements TravelOrderApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    TravelService travelService;

    @Autowired
    TravelOrderService travelOrderService;

    /***
     * 新增订单
     * @param scenicSpotOrder
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTravelOrder(@Valid @RequestBody ScenicSpotOrder scenicSpotOrder, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String dishame = "";
        String dishes = "";
        double money = 0.0;
        Date date = new Date();
        ScenicSpotTickets laf = null;
        ScenicSpotTickets dis = null;
        List iup = null;
        Map<String, Object> map = new HashMap<>();
        //查询景区 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVEL + scenicSpotOrder.getUserId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ScenicSpot kitchen = travelService.findReserve(scenicSpotOrder.getUserId());
            if (kitchen == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增订单失败,景区不存在", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen);
            redisUtils.hmset(Constants.REDIS_KEY_TRAVEL + kitchen.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
        }
        ScenicSpot kh = (ScenicSpot) CommonUtils.mapToObject(kitchenMap, ScenicSpot.class);
        if (!CommonUtils.checkFull(scenicSpotOrder.getTicketsIds()) && !CommonUtils.checkFull(scenicSpotOrder.getTicketsNumber())) {
            String[] sd = scenicSpotOrder.getTicketsIds().split(",");//门票ID
            String[] fn = scenicSpotOrder.getTicketsNumber().split(",");//门票数量
            if (sd != null && fn != null) {
                iup = travelService.findDishesList(sd);
                if (iup != null && iup.size() > 0) {
                    laf = (ScenicSpotTickets) iup.get(0);
                    if (laf != null && scenicSpotOrder.getMyId() != laf.getUserId() && iup.size() == sd.length) {
                        for (int i = 0; i < iup.size(); i++) {
                            dis = (ScenicSpotTickets) iup.get(i);
                            for (int j = 0; j < sd.length; j++) {
                                if (dis.getId() == Long.parseLong(sd[j])) {//确认是当前门票ID
                                    double cost = dis.getCost();//单价
                                    dishame = dis.getName();//门票名称
                                    dishes += dis.getId() + "," + dishame + "," + Integer.parseInt(fn[j]) + "," + cost + (i == iup.size() - 1 ? "" : ";");//门票ID,名称,数量,价格;
                                    money += Integer.parseInt(fn[j]) * cost;//总价格
                                }
                            }
                        }
                        if (money <= 0) {
                            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "订单总金额不能为0，请核实后重新下单！", new JSONObject());
                        }
                    }
                }
            }
        }
        if (kh != null) {
            long time = date.getTime();
            String noTime = String.valueOf(time);
            String random = CommonUtils.getRandom(6, 1);
            String noRandom = CommonUtils.strToMD5(noTime + scenicSpotOrder.getMyId() + random, 16);
            scenicSpotOrder.setNo(noRandom);//订单编号【MD5】

            String random2 = CommonUtils.getRandom(6, 1);
            String noRandom2 = CommonUtils.strToMD5(noTime + scenicSpotOrder.getMyId() + random2, 16);
            scenicSpotOrder.setVoucherCode(noRandom2);//凭证码【MD5】
            scenicSpotOrder.setAddTime(date);
            scenicSpotOrder.setScenicSpotId(kh.getId());
            scenicSpotOrder.setScenicSpotName(kh.getScenicSpotName());
            if (!CommonUtils.checkFull(kh.getPicture())) {
                String[] strings = kh.getPicture().split(",");
                scenicSpotOrder.setSmallMap(strings[0]);
            }
            scenicSpotOrder.setDishameCost(dishes);//名称,数量,价格
            scenicSpotOrder.setMoney(money);//总价
            travelOrderService.addOrders(scenicSpotOrder);
            map.put("infoId", scenicSpotOrder.getNo());

            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(scenicSpotOrder);
            redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + scenicSpotOrder.getMyId() + "_" + scenicSpotOrder.getNo(), ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @Override
    public ReturnData delTravelOrder(@PathVariable long id) {
        ScenicSpotOrder io = travelOrderService.findById(id, CommonUtils.getMyId(), 0);
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
        //清除缓存中的景区订单信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + io.getNo(), 0);
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
    public ReturnData receiptTravel(@PathVariable long id, @PathVariable String voucherCode) {
        ScenicSpotOrder io = travelOrderService.findById(id, CommonUtils.getMyId(), 1);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        if (io.getPaymentStatus() == 0) {//未付款
            return returnData(StatusCode.CODE_TRAVEL_NOPAYMENT.CODE_VALUE, "您的订单尚未支付，请尽快支付再扫码", io);
        }
        if (io.getOrdersType() == 1) {//防止多次验票成功后多次打款
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = dateFormat.format(io.getInspectTicketTime());
            return returnData(StatusCode.CODE_TRAVEL_REPEAT.CODE_VALUE, "您已于" + time + "扫码验票成功", io);
        }
        if (io.getOrdersType() == 0) {//已付款未验票
            if (io.getUserId() != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限验票", io);
            }
            if (!io.getVoucherCode().equals(voucherCode)) {
                return returnData(StatusCode.CODE_TRAVEL_INVALID.CODE_VALUE, "门票无效", io);
            }
            //格式化为相同格式
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String DateStr1 = dateFormat.format(io.getPlayTime());
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
                return returnData(StatusCode.CODE_TRAVEL_INVALID.CODE_VALUE, "门票无效", io);
            }
            int i = dateTime1.compareTo(dateTime2);
            if (i > 0) {
                io.setOrdersType(6);
                io.setUpdateCategory(4);
                travelOrderService.updateOrders(io);
                //景区订单放入缓存
                Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
                return returnData(StatusCode.CODE_TRAVEL_ADVANCE.CODE_VALUE, "您还没到游玩时间 ", io);
            } else if (i < 0) {
                return returnData(StatusCode.CODE_TRAVEL_BE_OVERDUE.CODE_VALUE, "您的门票已过期", io);
            } else {
                io.setOrdersType(1);
                //由未验票改为已验票
                io.setInspectTicketTime(new Date());
                io.setUpdateCategory(1);
                travelOrderService.updateOrders(io);
                //商家入账
                mqUtils.sendPurseMQ(io.getUserId(), 35, 0, io.getMoney());
                //清除缓存中的景区 订单信息
                redisUtils.expire(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + io.getNo(), 0);
                //景区订单放入缓存
                Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
                redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", io);
    }

    /***
     * 更改订单状态
     * 由已验票改为已完成
     * @param id  订单Id
     * @return
     */
    @Override
    public ReturnData completeTravel(@PathVariable long id) {
        ScenicSpotOrder io = travelOrderService.findById(id, CommonUtils.getMyId(), 2);
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
        }
        //由已验票改为已完成
        io.setOrdersType(2);        //已完成
        io.setCompleteTime(new Date());
        io.setUpdateCategory(2);
        travelOrderService.updateOrders(io);
        //清除缓存中的景区订单信息
        redisUtils.expire(Constants.REDIS_KEY_TRAVELORDERS + CommonUtils.getMyId() + "_" + io.getNo(), 0);
        //景区订单放入缓存
        Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
        redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + CommonUtils.getMyId() + "_" + io.getNo(), ordersMap, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @Override
    public ReturnData findTravelOrder(@PathVariable String no) {
        //查询缓存 缓存中不存在 查询数据库
        ScenicSpotOrder io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_TRAVELORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = travelOrderService.findNo(no);
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
            ordersMap = objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ordersMap);
    }

    /***
     * 对象转map
     * @param obj
     * @return
     * @throws Exception
     */
    public Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap();
        try {
            if (obj == null) {
                return null;
            }
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.get(obj) instanceof Date) {//判断是否为时间格式
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    String date = "";
                    if (field.getName().equals("playTime")) {
                        date = sdf2.format(field.get(obj));
                    } else {
                        date = sdf.format(field.get(obj));
                    }
                    map.put(field.getName(), date);
                } else {
                    map.put(field.getName(), field.get(obj));
                }
            }
            return map;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @Override
    public ReturnData cancelTravelOrders(@PathVariable long id) {
        ScenicSpotOrder ko = null;
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
            long time = ko.getPlayTime().getTime();
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
                mqUtils.sendPurseMQ(ko.getMyId(), 35, 0, ko.getMoney());
            }
            //清除缓存中的景区 订单信息
            redisUtils.expire(Constants.REDIS_KEY_TRAVELORDERS + ko.getMyId() + "_" + ko.getNo(), 0);
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(ko);
            redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + ko.getMyId() + "_" + ko.getNo(), ordersMap, Constants.USER_TIME_OUT);
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
    public ReturnData findTravelOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ScenicSpotOrder> pageBean;
        pageBean = travelOrderService.findOrderList(identity, userId, ordersType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        ScenicSpotOrder t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (ScenicSpotOrder) list.get(i);
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
     * 添加评论
     * @param shopTravelComment
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTravelComment(@Valid @RequestBody ScenicSpotComment shopTravelComment, BindingResult bindingResult) {
        //查询该景区信息
        //查询缓存 缓存中不存在 查询数据库
        ScenicSpot posts = travelService.findById(shopTravelComment.getMasterId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "评价景区失败，景区不存在！", new JSONObject());
        }
        //处理特殊字符
        String content = shopTravelComment.getContent();
        if (!CommonUtils.checkFull(content)) {
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "评论内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            shopTravelComment.setContent(filteringContent);
        }
        if (!CommonUtils.checkFull(posts.getPicture())) {
            String[] strings = posts.getPicture().split(",");
            shopTravelComment.setImgUrls(strings[0]);
        }
        shopTravelComment.setTime(new Date());
        if (shopTravelComment.getReplyType() == 0) {//新增评论
            //查询该景区订单信息
            ScenicSpotOrder io = travelOrderService.findById(shopTravelComment.getOrderId(), CommonUtils.getMyId(), -1);
            if (io == null || io.getMyId() != CommonUtils.getMyId() || (io.getOrdersType() != 1 && io.getOrdersType() != 2)) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "订单不存在！", new JSONObject());
            }
            //更新订单状态为已评价
            io.setOrdersType(3);
            io.setUpdateCategory(4);
            travelOrderService.updateOrders(io);
            //清除缓存中的景区 订单信息
            redisUtils.expire(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + io.getNo(), 0);
            //订单信息放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_TRAVELORDERS + io.getMyId() + "_" + io.getNo(), ordersMap, Constants.USER_TIME_OUT);
            //放入缓存(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_TRAVEL_COMMENT + posts.getId(), shopTravelComment, Constants.USER_TIME_OUT);
        } else {//新增回复
            List list = null;
            //先添加到缓存集合(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_TRAVEL_REPLY + shopTravelComment.getFatherId(), shopTravelComment, Constants.USER_TIME_OUT);
            //再保证5条数据
            list = redisUtils.getList(Constants.REDIS_KEY_TRAVEL_REPLY + shopTravelComment.getFatherId(), 0, -1);
            //清除缓存中的回复信息
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL_REPLY + shopTravelComment.getFatherId(), 0);
            if (list != null && list.size() > 5) {//限制五条回复
                //缓存中获取最新五条回复
                ScenicSpotComment message = null;
                List<ScenicSpotComment> messageList = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (j < 5) {
                        message = (ScenicSpotComment) list.get(j);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                }
                if (messageList.size() == 5) {
                    redisUtils.pushList(Constants.REDIS_KEY_TRAVEL_REPLY + shopTravelComment.getFatherId(), messageList, 0);
                }
            }
            //更新回复数
            ScenicSpotComment num = travelOrderService.findById(shopTravelComment.getFatherId());
            if (num != null) {
                shopTravelComment.setReplyNumber(num.getReplyNumber() + 1);
                travelOrderService.updateCommentNum(shopTravelComment);
            }
        }
        travelOrderService.addComment(shopTravelComment);
        //更新评论数
        posts.setTotalEvaluate(posts.getTotalEvaluate() + 1);
        travelOrderService.updateBlogCounts(posts);
        //更新评论总分、平均分
        List list1 = travelOrderService.findCommentList(shopTravelComment.getMasterId());
        if (list1 != null && list1.size() > 0) {
            long score = 0;//总分
            double averageScore = 0;   // 平均评分
            for (int i = 0; i < list1.size(); i++) {
                ScenicSpotComment kitchenEvaluate = (ScenicSpotComment) list1.get(i);
                if (kitchenEvaluate == null) {
                    continue;
                }
                score += kitchenEvaluate.getScore();
            }
            //更新总评分
            posts.setTotalScore(score);
            //更新评论平均分
            averageScore = score / list1.size();
            posts.setAverageScore((int) Math.round(averageScore));
            travelService.updateScore(posts);
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL + posts.getUserId(), 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除评论
     * @param id 评论ID
     * @param goodsId 景区ID
     * @return
     */
    @Override
    public ReturnData delTravelComment(@PathVariable long id, @PathVariable long goodsId) {
        List list = null;
        List list2 = null;
        List messList = null;
        ScenicSpotComment comment = travelOrderService.findById(id);
        if (comment == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "评论不存在", new JSONArray());
        }
        //查询该景区信息
        ScenicSpot posts = travelService.findById(goodsId);
        if (posts == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "删除评价失败，景区不存在！", new JSONObject());
        }
        //判断操作人权限
        long myId = CommonUtils.getMyId();//操作者ID
        long userId = comment.getUserId();//被删除者ID
        if (myId != userId) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "参数有误，当前用户[" + myId + "]无权限删除用户[" + userId + "]的评价", new JSONObject());
        }
        comment.setReplyStatus(1);//1删除
        travelOrderService.update(comment);
        //同时删除此评论下回复
        String ids = "";
        messList = travelOrderService.findMessList(id);
        if (messList != null && messList.size() > 0) {
            for (int i = 0; i < messList.size(); i++) {
                ScenicSpotComment message = null;
                message = (ScenicSpotComment) messList.get(i);
                if (message != null) {
                    ids += message.getId() + ",";
                }
            }
            //更新回复删除状态
            travelOrderService.updateReplyState(ids.split(","));
        }
        //更新景区评论数
        int num = messList.size();
        posts.setTotalScore(posts.getTotalScore() - num - 1);
        travelOrderService.updateBlogCounts(posts);
        if (comment.getReplyType() == 0) {
            //获取缓存中评论列表
            list = redisUtils.getList(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, 0, -1);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    ScenicSpotComment comment2 = (ScenicSpotComment) list.get(i);
                    if (comment2.getId() == id) {
                        //更新评论缓存
                        redisUtils.removeList(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, 1, comment2);
                        //更新此评论下的回复缓存
                        redisUtils.expire(Constants.REDIS_KEY_TRAVEL_REPLY + comment.getFatherId(), 0);
                        break;
                    }
                }
            }
        } else {
            List<ScenicSpotComment> messageList = new ArrayList<>();
            //清除缓存中的回复信息
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL_REPLY + comment.getFatherId(), 0);
            //清除缓存中评论列表
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, 0);
            //数据库获取最新五条回复
            list2 = travelOrderService.findMessList(comment.getFatherId());
            if (list2 != null && list2.size() > 0) {
                ScenicSpotComment message = null;
                for (int j = 0; j < list2.size(); j++) {
                    if (j < 5) {
                        message = (ScenicSpotComment) list2.get(j);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                }
                redisUtils.pushList(Constants.REDIS_KEY_TRAVEL_REPLY + comment.getFatherId(), messageList, 0);
            }
            //更新回复数
            ScenicSpotComment floorComment = travelOrderService.findById(comment.getFatherId());
            if (floorComment != null) {
                floorComment.setReplyNumber(floorComment.getReplyNumber() - 1);
                travelOrderService.updateCommentNum(floorComment);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询评论记录
     * @param goodsId     景区ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findTravelCommentList(@PathVariable long goodsId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //获取缓存中评论列表
        List list = null;
        List list2 = null;
        List commentList = null;
        List commentList2 = null;
        List<ScenicSpotComment> messageArrayList = new ArrayList<>();
        PageBean<ScenicSpotComment> pageBean = null;
        long countTotal = redisUtils.getListSize(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId);
        int pageCount = page * count;
        if (pageCount > countTotal) {
            pageCount = -1;
        } else {
            pageCount = pageCount - 1;
        }
        commentList = redisUtils.getList(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, (page - 1) * count, pageCount);
        //获取数据库中评论列表
        if (commentList == null || commentList.size() < count) {
            pageBean = travelOrderService.findList(goodsId, page, count);
            commentList2 = pageBean.getList();
            if (commentList2 != null && commentList2.size() > 0) {
                for (int i = 0; i < commentList2.size(); i++) {
                    ScenicSpotComment comment = null;
                    comment = (ScenicSpotComment) commentList2.get(i);
                    if (comment != null) {
                        for (int j = 0; j < commentList.size(); j++) {
                            ScenicSpotComment comment2 = null;
                            comment2 = (ScenicSpotComment) commentList.get(j);
                            if (comment2 != null) {
                                if (comment.getId() == comment2.getId()) {
                                    redisUtils.removeList(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, 1, comment2);
                                }
                            }
                        }
                    }
                }
                //更新缓存
                redisUtils.pushList(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, commentList2, Constants.USER_TIME_OUT);
                //获取最新缓存
                commentList = redisUtils.getList(Constants.REDIS_KEY_TRAVEL_COMMENT + goodsId, (page - 1) * count, page * count);
            }
        }
        if (commentList == null) {
            commentList = new ArrayList();
        }
        for (int j = 0; j < commentList.size(); j++) {//评论
            UserInfo userInfo = null;
            ScenicSpotComment comment = null;
            comment = (ScenicSpotComment) commentList.get(j);
            if (comment != null) {
                userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                if (userInfo != null) {
                    comment.setUserHead(userInfo.getHead());
                    comment.setUserName(userInfo.getName());
                    comment.setHouseNumber(userInfo.getHouseNumber());
                    comment.setProTypeId(userInfo.getProType());
                }
                //获取缓存中回复列表
                list = redisUtils.getList(Constants.REDIS_KEY_TRAVEL_REPLY + comment.getId(), 0, -1);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {//回复
                        ScenicSpotComment message = null;
                        message = (ScenicSpotComment) list.get(i);
                        if (message != null) {
                            userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                            if (userInfo != null) {
                                message.setReplayName(userInfo.getName());
                            }
                            userInfo = userInfoUtils.getUserInfo(message.getUserId());
                            if (userInfo != null) {
                                message.setUserName(userInfo.getName());
                            }
                        }
                    }
                    comment.setMessageList(list);
                } else {
                    //查询数据库 （获取最新五条回复）
                    list2 = travelOrderService.findMessList(comment.getId());
                    if (list2 != null && list2.size() > 0) {
                        ScenicSpotComment message = null;
                        for (int l = 0; l < list2.size(); l++) {
                            if (l < 5) {
                                message = (ScenicSpotComment) list2.get(l);
                                if (message != null) {
                                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                                    if (userInfo != null) {
                                        message.setReplayName(userInfo.getName());
                                    }
                                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                                    if (userInfo != null) {
                                        message.setUserName(userInfo.getName());
                                    }
                                    messageArrayList.add(message);
                                }
                            }
                        }
                        comment.setMessageList(messageArrayList);
                        //更新缓存
                        redisUtils.pushList(Constants.REDIS_KEY_TRAVEL_REPLY + comment.getId(), messageArrayList, 0);
                    }
                }
            }
        }
        pageBean = new PageBean<>();
        pageBean.setSize(commentList.size());
        pageBean.setPageNum(page);
        pageBean.setPageSize(count);
        pageBean.setList(commentList);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findTravelReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<ScenicSpotComment> pageBean = null;
        pageBean = travelOrderService.findReplyList(contentId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        long num = 0;
        UserInfo userInfo = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {//回复
                ScenicSpotComment message = null;
                message = (ScenicSpotComment) list.get(i);
                if (message != null) {
                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                    if (userInfo != null) {
                        message.setReplayName(userInfo.getName());
                    }
                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                    if (userInfo != null) {
                        message.setUserHead(userInfo.getHead());
                        message.setUserName(userInfo.getName());
                        message.setProTypeId(userInfo.getProType());
                        message.setHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
            //消息
            num = travelOrderService.getReplayCount(contentId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        map.put("list", list);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
