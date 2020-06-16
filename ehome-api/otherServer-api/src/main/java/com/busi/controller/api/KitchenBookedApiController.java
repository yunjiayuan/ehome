package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房订座相关接口
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 16:40
 */
public interface KitchenBookedApiController {

    /***
     * 新增厨房订座
     * @param kitchenReserve
     * @return
     */
    @PostMapping("addReserve")
    ReturnData addReserve(@Valid @RequestBody KitchenReserve kitchenReserve, BindingResult bindingResult);

    /***
     * 新增订座数据
     * @param kitchenReserve
     * @return
     */
    @PostMapping("addReserveData")
    ReturnData addReserveData(@Valid @RequestBody KitchenReserveData kitchenReserve, BindingResult bindingResult);

    /***
     * 编辑厨房订座
     * @param kitchenReserve
     * @return
     */
    @PutMapping("changeReserve")
    ReturnData changeReserve(@Valid @RequestBody KitchenReserve kitchenReserve, BindingResult bindingResult);

    /**
     * @Description: 删除厨房订座
     * @return:
     */
    @DeleteMapping("delReserve/{userId}/{id}")
    ReturnData delReserve(@PathVariable long userId, @PathVariable long id);

    /***
     * 更新厨房订座营业状态
     * @param kitchenReserve
     * @return
     */
    @PutMapping("updReserveStatus")
    ReturnData updReserveStatus(@Valid @RequestBody KitchenReserve kitchenReserve, BindingResult bindingResult);

    /***
     * 查询厨房订座信息
     * @param userId
     * @return
     */
    @GetMapping("findReserve/{userId}")
    ReturnData findReserve(@PathVariable long userId);

    /***
     * 查询订座数据详情
     * @param id
     * @return
     */
    @GetMapping("findReserveData/{id}")
    ReturnData findReserveData(@PathVariable long id);

    /***
     * 认领店铺
     * @param realName  店主姓名
     * @param phone  店主电话
     * @return
     */
    @GetMapping("claimKitchen/{realName}/{phone}")
    ReturnData claimKitchen(@PathVariable String realName, @PathVariable String phone);

    /***
     * 查询厨房订座数据列表
     * @param kitchenName    厨房名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findReserveDataList/{kitchenName}/{lat}/{lon}/{page}/{count}")
    ReturnData findReserveDataList(@PathVariable String kitchenName, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 条件查询厨房订座
     * @param cuisine    菜系
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType 排序类型：默认【0综合排序】   0综合排序  1距离最近  2服务次数最高  3评分最高
     * @param kitchenName    厨房名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findReserveList/{cuisine}/{watchVideos}/{sortType}/{kitchenName}/{lat}/{lon}/{page}/{count}")
    ReturnData findReserveList(@PathVariable String cuisine, @PathVariable int watchVideos, @PathVariable int sortType, @PathVariable String kitchenName, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);


    /***
     * 新增订座信息
     * @param kitchenBooked
     * @return
     */
    @PostMapping("addKitchenBooked")
    ReturnData addKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult);

    /***
     * 查看订座设置详情
     * @param userId  商家ID
     * @return
     */
    @GetMapping("findKitchenBooked/{userId}")
    ReturnData findKitchenBooked(@PathVariable long userId);

    /***
     * 编辑订座设置
     * @param kitchenBooked
     * @return
     */
    @PutMapping("changeKitchenBooked")
    ReturnData changeKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult);

    /***
     * 新增包间or大厅信息
     * @param kitchenPrivateRoom
     * @return
     */
    @PostMapping("addPrivateRoom")
    ReturnData addPrivateRoom(@Valid @RequestBody KitchenPrivateRoom kitchenPrivateRoom, BindingResult bindingResult);

    /***
     * 查询包间or大厅信息
     * @param id
     * @return
     */
    @GetMapping("findRoom/{id}")
    ReturnData findRoom(@PathVariable long id);

    /***
     * 查看包间or大厅列表
     * @param eatTime  就餐时间
     * @param userId  商家ID
     * @param bookedType  包间0  散桌1
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPrivateRoom/{eatTime}/{userId}/{bookedType}/{page}/{count}")
    ReturnData findPrivateRoom(@PathVariable String eatTime, @PathVariable long userId, @PathVariable int bookedType, @PathVariable int page, @PathVariable int count);

//    /***
//     * 查看包间or大厅是否可预订
//     * @param eatTime  就餐时间
//     * @param id  包间or大厅ID
//     * @return
//     */
//    @GetMapping("findTable/{eatTime}/{id}")
//    ReturnData findTable(@PathVariable Date eatTime, @PathVariable long id);


    /***
     * 编辑包间or大厅信息
     * @param kitchenPrivateRoom
     * @return
     */
    @PutMapping("changePrivateRoom")
    ReturnData changePrivateRoom(@Valid @RequestBody KitchenPrivateRoom kitchenPrivateRoom, BindingResult bindingResult);

    /**
     * @Description: 删除包间or大厅
     * @return:
     */
    @DeleteMapping("delPrivateRoom/{ids}")
    ReturnData delPrivateRoom(@PathVariable String ids);

    /***
     * 新增菜品
     * @param kitchenDishes
     * @return
     */
    @PostMapping("addDishes")
    ReturnData addDishes(@Valid @RequestBody KitchenReserveDishes kitchenDishes, BindingResult bindingResult);

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @PutMapping("updateDishes")
    ReturnData updateDishes(@Valid @RequestBody KitchenReserveDishes kitchenDishes, BindingResult bindingResult);

    /**
     * @Description: 删除菜品
     * @return:
     */
    @DeleteMapping("delDishes/{ids}")
    ReturnData delDishes(@PathVariable String ids);

    /***
     * 查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("detailsDishes/{id}")
    ReturnData detailsDishes(@PathVariable long id);

    /***
     * 分页查询菜品列表
     * @param kitchenId   厨房ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findReserveDishesList/{kitchenId}/{page}/{count}")
    ReturnData findReserveDishesList(@PathVariable long kitchenId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增上菜时间
     * @param kitchenServingTime
     * @return
     */
    @PostMapping("addUpperTime")
    ReturnData addUpperTime(@Valid @RequestBody KitchenServingTime kitchenServingTime, BindingResult bindingResult);

    /***
     * 更新上菜时间
     * @param kitchenServingTime
     * @return
     */
    @PutMapping("updateUpperTime")
    ReturnData updateUpperTime(@Valid @RequestBody KitchenServingTime kitchenServingTime, BindingResult bindingResult);

    /***
     * 查询上菜时间列表
     * @param kitchenId   厨房ID
     * @return
     */
    @GetMapping("findUpperTime/{kitchenId}")
    ReturnData findUpperTime(@PathVariable long kitchenId);

}
