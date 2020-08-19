package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HotelService;
import com.busi.service.HotelTourismService;
import com.busi.service.TravelService;
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
    TravelService travelService;

    @Autowired
    HotelService hotelService;

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
     * 查询上菜时间列表
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
            //新增默认菜品分类
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
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
