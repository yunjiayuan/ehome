package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 厨房相关接口
 * author：zhaojiajie
 * create time：2019-3-1 10:35:22
 */
public interface KitchenApiController {

    /***
     * 新增厨房
     * @param kitchen
     * @return
     */
    @PostMapping("addKitchen")
    ReturnData addKitchen(@Valid @RequestBody Kitchen kitchen, BindingResult bindingResult);

    /***
     * 编辑厨房
     * @param kitchen
     * @return
     */
    @PutMapping("changeKitchen")
    ReturnData changeKitchen(@Valid @RequestBody Kitchen kitchen, BindingResult bindingResult);

    /**
     * @Description: 删除厨房
     * @return:
     */
    @DeleteMapping("delKitchen/{userId}/{id}")
    ReturnData delKitchen(@PathVariable long userId, @PathVariable long id);

    /***
     * 更新厨房营业状态
     * @param kitchen
     * @return
     */
    @PutMapping("updKitchenStatus")
    ReturnData updKitchenStatus(@Valid @RequestBody Kitchen kitchen, BindingResult bindingResult);

    /***
     * 查询厨房信息
     * @param userId
     * @return
     */
    @GetMapping("findKitchen/{userId}/{bookedState}")
    ReturnData findKitchen(@PathVariable long userId, @PathVariable int bookedState);

    /***
     * 条件查询厨房
     * @param lat      纬度
     * @param lon      经度
     * @param kitchenName    厨房名称
     * @param page     页码
     * @param count    条数
     * @param watchVideos 筛选视频：0否 1是
     * @param watchBooked 筛选订座：0否 1是
     * @param sortType 排序类型：默认【0综合排序】   0综合排序  1距离最近  2服务次数最高  3评分最高
     * @return
     */
    @GetMapping("findKitchenList/{watchVideos}/{watchBooked}/{sortType}/{kitchenName}/{lat}/{lon}/{page}/{count}")
    ReturnData findKitchenList(@PathVariable int watchVideos, @PathVariable int watchBooked, @PathVariable int sortType, @PathVariable String kitchenName, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 检测实名状态
     * @param userId
     * @return
     */
    @GetMapping("realNameStatus/{userId}")
    ReturnData realNameStatus(@PathVariable long userId);

    /***
     * 新增厨房收藏
     * @param kitchenCollection
     * @return
     */
    @PostMapping("addKitchenCollect")
    ReturnData addKitchenCollect(@Valid @RequestBody KitchenCollection kitchenCollection, BindingResult bindingResult);

    /***
     * 分页查询用户收藏列表
     * @param userId   用户ID
     * @param bookedState   0厨房  1订座
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findKitchenCollectList/{userId}/{bookedState}/{lat}/{lon}/{page}/{count}")
    ReturnData findKitchenCollectList(@PathVariable long userId,@PathVariable int bookedState, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除收藏
     * @return:
     */
    @DeleteMapping("delKitchenCollect/{ids}")
    ReturnData delKitchenCollect(@PathVariable String ids);

    /***
     * 新增菜品
     * @param kitchenDishes
     * @return
     */
    @PostMapping("addFood")
    ReturnData addFood(@Valid @RequestBody KitchenDishes kitchenDishes, BindingResult bindingResult);

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @PutMapping("updateFood")
    ReturnData updateFood(@Valid @RequestBody KitchenDishes kitchenDishes, BindingResult bindingResult);

    /**
     * @Description: 删除菜品
     * @return:
     */
    @DeleteMapping("delFood/{ids}")
    ReturnData delFood(@PathVariable String ids);

    /***
     * 查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("disheSdetails/{id}")
    ReturnData disheSdetails(@PathVariable long id);

    /***
     * 分页查询菜品列表
     * @param kitchenId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findDishesList/{kitchenId}/{page}/{count}")
    ReturnData findDishesList(@PathVariable long kitchenId, @PathVariable int page, @PathVariable int count);

}
