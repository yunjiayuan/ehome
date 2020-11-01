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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 酒店景区订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 17:51
 */
@RestController
public class HotelTourismController extends BaseController implements HotelTourismApiController {

    @Autowired
    HotelTourismService kitchenBookedService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    KitchenBookedService bookedService;

    @Autowired
    TravelService travelService;

    @Autowired
    HotelService hotelService;

    @Autowired
    PharmacyService pharmacyService;

    @Autowired
    HotelTourismBookedOrdersService hotelTourismBookedOrdersService;

    /***
     * 新增订座设置信息
     * @param kitchenBooked
     * @return
     */
    @Override
    public ReturnData addHotelTourismBooked(@Valid @RequestBody KitchenReserveBooked kitchenBooked, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        KitchenReserveBooked kitchen = kitchenBookedService.findByUserId(kitchenBooked.getUserId(), kitchenBooked.getType());
        if (kitchen != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "你已经设置过了", new JSONObject());
        }
        //新增酒店景区订座信息
        kitchenBookedService.add(kitchenBooked);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订座设置详情
     * @param userId  商家ID
     * @param type  0酒店 1景区
     * @return
     */
    @Override
    public ReturnData findHotelTourismBooked(@PathVariable long userId, @PathVariable int type) {
        KitchenReserveBooked kitchen = kitchenBookedService.findByUserId(userId, type);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchen);
    }

    /***
     * 编辑订座设置
     * @param kitchenBooked
     * @return
     */
    @Override
    public ReturnData changeHotelTourismBooked(@Valid @RequestBody KitchenReserveBooked kitchenBooked, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenBookedService.updateBooked(kitchenBooked);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增包间or大厅信息
     * @param kitchenPrivateRoom
     * @return
     */
    @Override
    public ReturnData addHotelTourismRoom(@Valid @RequestBody KitchenReserveRoom kitchenPrivateRoom, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(kitchenPrivateRoom.getImgUrl())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (kitchenPrivateRoom.getImgUrl().split(",").length > 9) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //新增酒店景区包间or大厅信息
        kitchenBookedService.addPrivateRoom(kitchenPrivateRoom);
        //更新酒店景区包间or大厅数量（+1）
        KitchenReserveBooked booked = kitchenBookedService.findByUserId(kitchenPrivateRoom.getUserId(), kitchenPrivateRoom.getType());
        if (booked != null) {
            if (kitchenPrivateRoom.getBookedType() == 1) {//就餐位置 包间0  散桌1
                booked.setLooseTableTotal(booked.getLooseTableTotal() + 1);
            } else {
                booked.setRoomsTotal(booked.getRoomsTotal() + 1);
            }
            kitchenBookedService.updatePosition(booked);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询包间or大厅信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findHotelTourismRoom(@PathVariable long id) {
        KitchenReserveRoom privateRoom = kitchenBookedService.findPrivateRoom(id);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", privateRoom);
    }

    /***
     * 查看包间or大厅列表
     * @param type  0酒店 1景区
     * @param eatTime  就餐时间
     * @param userId  商家ID
     * @param bookedType  包间0  散桌1
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHotelTourismRoomList(@PathVariable int type, @PathVariable String eatTime, @PathVariable long userId, @PathVariable int bookedType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<KitchenReserveRoom> pageBean = null;
        pageBean = kitchenBookedService.findRoomList(type, userId, bookedType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null && list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        if (CommonUtils.checkFull(eatTime)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(eatTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //查询订单中已订餐桌（用来判断是否可预订，PS:有可能不同时段不同人预定）
        List ordersList = null;
        ordersList = hotelTourismBookedOrdersService.findOrdersList(type, userId, date, bookedType);
        if (ordersList == null && ordersList.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
        }
        for (int i = 0; i < list.size(); i++) {
            KitchenReserveRoom privateRoom = (KitchenReserveRoom) list.get(i);
            if (privateRoom == null) {
                continue;
            }
            for (int j = 0; j < ordersList.size(); j++) {
                KitchenBookedOrders kh = (KitchenBookedOrders) ordersList.get(j);
                if (kh == null) {
                    continue;
                }
                if (kh.getPositionId() == privateRoom.getId()) {
                    privateRoom.setReserveState(1);//已预定
                    continue;
                }
            }
        }
        //按是否预定正序排序
        Collections.sort(list, new Comparator<KitchenReserveRoom>() {
            /*
             * int compare(Person o1, Person o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和p2相等，
             * 返回正数表示：o1大于o2
             */
            @Override
            public int compare(KitchenReserveRoom o1, KitchenReserveRoom o2) {
                // 按照预定与否进行正序排列
                if (o1.getReserveState() > o2.getReserveState()) {
                    return 1;
                }
                if (o1.getReserveState() == o2.getReserveState()) {
                    return 0;
                }
                return -1;
            }
        });
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 编辑包间or大厅信息
     * @param kitchenPrivateRoom
     * @return
     */
    @Override
    public ReturnData changeHotelTourismRoom(@Valid @RequestBody KitchenReserveRoom kitchenPrivateRoom, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenBookedService.upPrivateRoom(kitchenPrivateRoom);
        if (!CommonUtils.checkFull(kitchenPrivateRoom.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(kitchenPrivateRoom.getUserId(), kitchenPrivateRoom.getDelImgUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除包间or大厅
     * @return:
     */
    @Override
    public ReturnData delHotelTourismRoom(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String[] idss = ids.split(",");
        long id = Long.parseLong(idss[0]);
        KitchenReserveRoom privateRoom = kitchenBookedService.findPrivateRoom(id);
        if (privateRoom == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //更新酒店景区包间or大厅数量（+1）
        KitchenReserveBooked booked = kitchenBookedService.findByUserId(privateRoom.getUserId(), privateRoom.getType());
        if (booked != null) {
            if (privateRoom.getBookedType() == 1) {// 包间0  散桌1
                booked.setLooseTableTotal(booked.getLooseTableTotal() - idss.length);
            } else {
                booked.setRoomsTotal(booked.getRoomsTotal() - idss.length);
            }
            kitchenBookedService.updatePosition(booked);
        }
        //删除数据库
        kitchenBookedService.delPrivateRoom(idss, CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增上菜时间
     * @param kitchenServingTime
     * @return
     */
    @Override
    public ReturnData addHotelTourismTime(@Valid @RequestBody KitchenReserveServingTime kitchenServingTime, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //开始查询
        KitchenReserveServingTime servingTime = kitchenBookedService.findUpperTime(kitchenServingTime.getKitchenId(), kitchenServingTime.getType());
        if (servingTime != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您已新增过", new JSONArray());
        }
        String[] time = kitchenServingTime.getUpperTime().split(",");
        if (time.length > 15) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "最多只能添加15个预约时间", new JSONObject());
        }
        KitchenReserveBooked kitchen = kitchenBookedService.findByUserId(kitchenServingTime.getUserId(), kitchenServingTime.getType());
        if (kitchen != null) {
            for (int i = 0; i < time.length; i++) {
                //compareTo用来比较两个对象，如果o1小于o2，返回负数；等于o2，返回0；大于o2返回正数
                if (time[i].compareTo(kitchen.getEarliestTime()) < 0 || kitchen.getLatestTime().compareTo(time[i]) < 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
                }
            }
        }
        kitchenBookedService.addUpperTime(kitchenServingTime);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新上菜时间
     * @param kitchenServingTime
     * @return
     */
    @Override
    public ReturnData updateHotelTourismTime(@Valid @RequestBody KitchenReserveServingTime kitchenServingTime, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        String[] time = kitchenServingTime.getUpperTime().split(",");
        if (time.length > 15) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "最多只能添加15个预约时间", new JSONObject());
        }
        KitchenReserveBooked kitchen = kitchenBookedService.findByUserId(kitchenServingTime.getUserId(), kitchenServingTime.getType());
        if (kitchen != null) {
            for (int i = 0; i < time.length; i++) {//上菜时间要在营业时间范围内
                if (time[i].compareTo(kitchen.getEarliestTime()) < 0 || kitchen.getLatestTime().compareTo(time[i]) < 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
                }
            }
        }
        kitchenBookedService.updateUpperTime(kitchenServingTime);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询上菜时间
     * @param type  0酒店 1景区
     * @param kitchenId   酒店景区ID
     * @return
     */
    @Override
    public ReturnData findHotelTourismTime(@PathVariable int type, @PathVariable long kitchenId) {
        //开始查询
        KitchenReserveServingTime servingTime = kitchenBookedService.findUpperTime(kitchenId, type);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", servingTime);
    }

    /***
     * 更新景区、酒店、订座相关设置状态
     * @param type 更新类型： 0景区订房、1景区订座、2酒店订座
     * @param relation 0关闭  1开启
     * @param id   景区、酒店ID
     * @return
     */
    @Override
    public ReturnData relationSet(@PathVariable int type, @PathVariable int relation, @PathVariable long id) {
        if (type == 0) {
            ScenicSpot hotel = new ScenicSpot();
            hotel.setRelationReservation(-1);
            hotel.setUserId(CommonUtils.getMyId());
            hotel.setRelationHotel(relation);
            travelService.update(hotel);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL + CommonUtils.getMyId(), 0);
        }
        if (type == 1) {
            ScenicSpot hotel = new ScenicSpot();
            hotel.setUserId(CommonUtils.getMyId());
            hotel.setRelationHotel(-1);
            hotel.setRelationReservation(relation);
            travelService.update(hotel);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_TRAVEL + CommonUtils.getMyId(), 0);
        }
        if (type == 2) {
            Hotel hotel = new Hotel();
            hotel.setUserId(CommonUtils.getMyId());
            hotel.setRelationReservation(relation);
            hotelService.update(hotel);
            //清除酒店缓存
            redisUtils.expire(Constants.REDIS_KEY_HOTEL + CommonUtils.getMyId(), 0);
        }
        if (type == 2 || type == 1) {
            if (type == 1) {
                type = 3;
            }
            if (relation == 1) {
                //新增默认菜品分类
                //判断该用户是否有初始分类
                int num = kitchenService.findNum(type, id);
                if (num <= 0) {
                    String[] strings = {"特色菜", "凉菜", "热菜", "主食", "白酒", "红酒", "啤酒", "洋酒", "黄酒", "饮料", "水"};
                    for (int i = 0; i < strings.length; i++) {
                        KitchenDishesSort sort = new KitchenDishesSort();
                        sort.setName(strings[i]);
                        sort.setUserId(CommonUtils.getMyId());
                        sort.setKitchenId(id);
                        sort.setBookedState(type);
                        kitchenBookedService.addSort(sort);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 统计各种审核状态数量
     * @param type  0酒店 1景区 2药店 3订座 4厨房
     * @return
     */
    @Override
    public ReturnData countAuditType(@PathVariable int type) {
        //判断是否有审核权限
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels < 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始统计
        List list = null;
        int num = 0;
        int num1 = 0;
        int num2 = 0;
        if (type == 0) {
            list = kitchenBookedService.countAuditType();
            for (int i = 0; i < list.size(); i++) {
                Hotel hotel = (Hotel) list.get(i);
                if (hotel.getAuditType() == 0) {
                    num++;
                }
                if (hotel.getAuditType() == 1) {
                    num1++;
                }
                if (hotel.getAuditType() == 2) {
                    num2++;
                }
            }
        }
        if (type == 1) {
            list = kitchenBookedService.countAuditType1();
            for (int i = 0; i < list.size(); i++) {
                ScenicSpot hotel = (ScenicSpot) list.get(i);
                if (hotel.getAuditType() == 0) {
                    num++;
                }
                if (hotel.getAuditType() == 1) {
                    num1++;
                }
                if (hotel.getAuditType() == 2) {
                    num2++;
                }
            }
        }
        if (type == 2) {
            list = kitchenBookedService.countAuditType2();
            for (int i = 0; i < list.size(); i++) {
                Pharmacy hotel = (Pharmacy) list.get(i);
                if (hotel.getAuditType() == 0) {
                    num++;
                }
                if (hotel.getAuditType() == 1) {
                    num1++;
                }
                if (hotel.getAuditType() == 2) {
                    num2++;
                }
            }
        }
        if (type == 3) {
            list = kitchenBookedService.countAuditType3();
            for (int i = 0; i < list.size(); i++) {
                KitchenReserve hotel = (KitchenReserve) list.get(i);
                if (hotel.getAuditType() == 0) {
                    num++;
                }
                if (hotel.getAuditType() == 1) {
                    num1++;
                }
                if (hotel.getAuditType() == 2) {
                    num2++;
                }
            }
        }
        if (type == 4) {
            list = kitchenBookedService.countAuditType4();
            for (int i = 0; i < list.size(); i++) {
                Kitchen hotel = (Kitchen) list.get(i);
                if (hotel.getAuditType() == 0) {
                    num++;
                }
                if (hotel.getAuditType() == 1) {
                    num1++;
                }
                if (hotel.getAuditType() == 2) {
                    num2++;
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("auditType", num);//0待审核 1已审核通过 2未审核通过
        map.put("auditType1", num1);
        map.put("auditType2", num2);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询审核列表
     * @param type  0酒店 1景区 2药店 3订座 4厨房
     * @param auditType  0待审核 1已审核通过 2未审核通过
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findAuditTypeList(@PathVariable int type, @PathVariable int auditType, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //判断是否有权限
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels < 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始查询
        if (type == 0) {//0酒店
            PageBean<Hotel> pageBean = null;
            pageBean = kitchenBookedService.findAuditTypeList(auditType, lat, lon, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
            }
            List list = pageBean.getList();
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
            }
            for (int j = 0; j < list.size(); j++) {
                Hotel kc = (Hotel) list.get(j);
                if (kc != null) {
                    //计算距离
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(kc.getLon(), kc.getLat(), lon, lat));
                    kc.setDistance(distance);//距离/m
                }
            }
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(page);
            pageBean.setPageSize(count);
            pageBean.setList(list);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        }
        if (type == 1) {//1景区
            PageBean<ScenicSpot> pageBean = null;
            pageBean = kitchenBookedService.findAuditTypeList2(auditType, lat, lon, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
            }
            List list = pageBean.getList();
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
            }
            for (int j = 0; j < list.size(); j++) {
                ScenicSpot kc = (ScenicSpot) list.get(j);
                if (kc != null) {
                    //计算距离
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(kc.getLon(), kc.getLat(), lon, lat));
                    kc.setDistance(distance);//距离/m
                }
            }
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(page);
            pageBean.setPageSize(count);
            pageBean.setList(list);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        }
        if (type == 2) {//2药店
            PageBean<Pharmacy> pageBean = null;
            pageBean = kitchenBookedService.findAuditTypeList3(auditType, lat, lon, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
            }
            List list = pageBean.getList();
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
            }
            for (int j = 0; j < list.size(); j++) {
                Pharmacy kc = (Pharmacy) list.get(j);
                if (kc != null) {
                    //计算距离
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(kc.getLon(), kc.getLat(), lon, lat));
                    kc.setDistance(distance);//距离/m
                }
            }
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(page);
            pageBean.setPageSize(count);
            pageBean.setList(list);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        }
        if (type == 3) {//3订座
            PageBean<KitchenReserve> pageBean = null;
            pageBean = kitchenBookedService.findAuditTypeList4(auditType, lat, lon, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
            }
            List list = pageBean.getList();
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
            }
            for (int j = 0; j < list.size(); j++) {
                KitchenReserve kc = (KitchenReserve) list.get(j);
                if (kc != null) {
                    //计算距离
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(kc.getLon(), kc.getLat(), lon, lat));
                    kc.setDistance(distance);//距离/m
                }
            }
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(page);
            pageBean.setPageSize(count);
            pageBean.setList(list);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        }
        if (type == 4) {//4厨房
            PageBean<Kitchen> pageBean = null;
            pageBean = kitchenBookedService.findAuditTypeList5(auditType, lat, lon, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
            }
            List list = pageBean.getList();
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
            }
            for (int j = 0; j < list.size(); j++) {
                Kitchen kc = (Kitchen) list.get(j);
                if (kc != null) {
                    //计算距离
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(kc.getLon(), kc.getLat(), lon, lat));
                    kc.setDistance(distance);//距离/m
                }
            }
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(page);
            pageBean.setPageSize(count);
            pageBean.setList(list);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新审核状态
     * @param type  0酒店 1景区 2药店 3订座 4厨房
     * @param auditType  0审核通过 1审核未通过
     * @param id   酒店、景区、药店、订座、厨房 主键ID
     * @return
     */
    @Override
    public ReturnData changeAuditType(@PathVariable int type, @PathVariable int auditType, @PathVariable long id) {
        //判断是否有审核权限
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels < 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始更新
        int num = kitchenBookedService.changeAuditType(type, auditType, id);
        if (num > 0) {
            if (type == 0) {//0酒店
                Hotel hotel = hotelService.findById(id);
                //清除缓存中的信息
                redisUtils.expire(Constants.REDIS_KEY_HOTEL + hotel.getUserId(), 0);
                if (hotel != null && hotel.getAuditType() == 1 && auditType == 0) {
                    //判断是否有邀请码
                    long myId = hotel.getUserId();
                    double redPacketsMoney = 10;
                    String proId = "";
                    String shareCode = hotel.getInvitationCode();
                    if (!CommonUtils.checkFull(shareCode)) {
                        //判断第一位是否为0  邀请码格式为 001001518 前两位为省简称ID
                        if (shareCode.indexOf("0") != 0) {
                            proId = shareCode.substring(0, 2);
                        } else {
                            proId = shareCode.substring(1, 2);
                        }
                        long userId = 0;
                        Map<String, Object> userIdMap = redisUtils.hmget(Constants.REDIS_KEY_HOUSENUMBER);
                        if (userIdMap == null || userIdMap.size() <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "酒店邀请者不存在!", new JSONObject());
                        }
                        for (String key : userIdMap.keySet()) {
                            Object object = key;
                            if (object.toString().equals(proId + "_" + shareCode.substring(2))) {
                                userId = Long.valueOf(String.valueOf(userIdMap.get(key)));
                                break;
                            }
                        }
                        if (userId <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "酒店邀请者不存在!", new JSONObject());
                        }
                        //验证参数
                        if (myId == userId) {
                            return returnData(StatusCode.CODE_SHARE_CODE_ERROR2.CODE_VALUE, "邀请码有误,邀请码不能是自己的", new JSONObject());
                        }
                        //新增邀请者奖励记录
                        mqUtils.addRewardLog(userId, 13, 0, redPacketsMoney, 0);
                    }
                }
            }
            if (type == 1) {//1景区
                ScenicSpot hotel = travelService.findById(id);
                //清除缓存中的信息
                redisUtils.expire(Constants.REDIS_KEY_TRAVEL + hotel.getUserId(), 0);
                if (hotel != null && hotel.getAuditType() == 1 && auditType == 0) {
                    //判断是否有邀请码
                    long myId = hotel.getUserId();
                    double redPacketsMoney = 10;
                    String proId = "";
                    String shareCode = hotel.getInvitationCode();
                    if (!CommonUtils.checkFull(shareCode)) {
                        //判断第一位是否为0  邀请码格式为 001001518 前两位为省简称ID
                        if (shareCode.indexOf("0") != 0) {
                            proId = shareCode.substring(0, 2);
                        } else {
                            proId = shareCode.substring(1, 2);
                        }
                        long userId = 0;
                        Map<String, Object> userIdMap = redisUtils.hmget(Constants.REDIS_KEY_HOUSENUMBER);
                        if (userIdMap == null || userIdMap.size() <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "邀请者不存在!", new JSONObject());
                        }
                        for (String key : userIdMap.keySet()) {
                            Object object = key;
                            if (object.toString().equals(proId + "_" + shareCode.substring(2))) {
                                userId = Long.valueOf(String.valueOf(userIdMap.get(key)));
                                break;
                            }
                        }
                        if (userId <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "邀请者不存在!", new JSONObject());
                        }
                        //验证参数
                        if (myId == userId) {
                            return returnData(StatusCode.CODE_SHARE_CODE_ERROR2.CODE_VALUE, "邀请码有误,邀请码不能是自己的", new JSONObject());
                        }
                        //新增邀请者奖励记录
                        mqUtils.addRewardLog(userId, 12, 0, redPacketsMoney, 0);
                    }
                }
            }
            if (type == 2) {//2药店
                Pharmacy hotel = pharmacyService.findById(id);
                //清除缓存中的信息
                redisUtils.expire(Constants.REDIS_KEY_PHARMACY + hotel.getUserId(), 0);
                if (hotel != null && hotel.getAuditType() == 1 && auditType == 0) {
                    //判断是否有邀请码
                    long myId = hotel.getUserId();
                    double redPacketsMoney = 10;
                    String proId = "";
                    String shareCode = hotel.getInvitationCode();
                    if (!CommonUtils.checkFull(shareCode)) {
                        //判断第一位是否为0  邀请码格式为 001001518 前两位为省简称ID
                        if (shareCode.indexOf("0") != 0) {
                            proId = shareCode.substring(0, 2);
                        } else {
                            proId = shareCode.substring(1, 2);
                        }
                        long userId = 0;
                        Map<String, Object> userIdMap = redisUtils.hmget(Constants.REDIS_KEY_HOUSENUMBER);
                        if (userIdMap == null || userIdMap.size() <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "邀请者不存在!", new JSONObject());
                        }
                        for (String key : userIdMap.keySet()) {
                            Object object = key;
                            if (object.toString().equals(proId + "_" + shareCode.substring(2))) {
                                userId = Long.valueOf(String.valueOf(userIdMap.get(key)));
                                break;
                            }
                        }
                        if (userId <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "邀请者不存在!", new JSONObject());
                        }
                        //验证参数
                        if (myId == userId) {
                            return returnData(StatusCode.CODE_SHARE_CODE_ERROR2.CODE_VALUE, "邀请码有误,邀请码不能是自己的", new JSONObject());
                        }
                        //新增邀请者奖励记录
                        mqUtils.addRewardLog(userId, 14, 0, redPacketsMoney, 0);
                    }
                }
            }
            if (type == 3) {//3订座
                KitchenReserve serviceReserve = bookedService.findById(id);
                //清除缓存中的信息
                redisUtils.expire(Constants.REDIS_KEY_KITCHEN + serviceReserve.getUserId() + "_" + 1, 0);
                if (serviceReserve != null && serviceReserve.getAuditType() == 1 && auditType == 0) {
                    //判断是否有邀请码
                    double redPacketsMoney = 10;
                    String proId = "";
                    String shareCode = serviceReserve.getInvitationCode();
                    if (!CommonUtils.checkFull(shareCode)) {
                        //判断第一位是否为0  邀请码格式为 001001518 前两位为省简称ID
                        if (shareCode.indexOf("0") != 0) {
                            proId = shareCode.substring(0, 2);
                        } else {
                            proId = shareCode.substring(1, 2);
                        }
                        long userId = 0;
                        Map<String, Object> userIdMap = redisUtils.hmget(Constants.REDIS_KEY_HOUSENUMBER);
                        if (userIdMap == null || userIdMap.size() <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "邀请者不存在!", new JSONObject());
                        }
                        for (String key : userIdMap.keySet()) {
                            Object object = key;
                            if (object.toString().equals(proId + "_" + shareCode.substring(2))) {
                                userId = Long.valueOf(String.valueOf(userIdMap.get(key)));
                                break;
                            }
                        }
                        if (userId <= 0) {
                            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "邀请者不存在!", new JSONObject());
                        }
                        //验证参数
                        if (serviceReserve.getUserId() == userId) {
                            return returnData(StatusCode.CODE_SHARE_CODE_ERROR2.CODE_VALUE, "邀请码有误,邀请码不能是自己的", new JSONObject());
                        }
                        //新增邀请者奖励记录
                        mqUtils.addRewardLog(userId, 11, 0, redPacketsMoney, 0);
                    }
                }
            }
            if (type == 4) {//4厨房
                Kitchen serviceReserve = kitchenService.findById(id);
                //清除缓存中的信息
                redisUtils.expire(Constants.REDIS_KEY_KITCHEN + serviceReserve.getUserId() + "_" + 0, 0);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
