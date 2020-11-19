package com.busi.controller.api;

import com.busi.entity.DoorwayBusiness;
import com.busi.entity.DoorwayBusinessCollection;
import com.busi.entity.DoorwayBusinessCommodity;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 家门口商家相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-11-10 16:45:33
 */
public interface DoorwayBusinessApiController {

    /***
     * 新增商家
     * @param scenicSpot
     * @return
     */
    @PostMapping("addDoorwayBusiness")
    ReturnData addDoorwayBusiness(@Valid @RequestBody DoorwayBusiness scenicSpot, BindingResult bindingResult);

    /***
     * 更新商家
     * @param scenicSpot
     * @return
     */
    @PutMapping("changeDoorwayBusiness")
    ReturnData changeDoorwayBusiness(@Valid @RequestBody DoorwayBusiness scenicSpot, BindingResult bindingResult);

    /**
     * @Description: 删除商家
     * @return:
     */
    @DeleteMapping("delDoorwayBusiness/{userId}/{id}")
    ReturnData delDoorwayBusiness(@PathVariable long userId, @PathVariable long id);

    /***
     * 更新商家营业状态
     * @param scenicSpot
     * @return
     */
    @PutMapping("updDoorwayBusinessStatus")
    ReturnData updDoorwayBusinessStatus(@Valid @RequestBody DoorwayBusiness scenicSpot, BindingResult bindingResult);

    /***
     * 查询商家信息
     * @param userId
     * @return
     */
    @GetMapping("findDoorwayBusiness/{userId}")
    ReturnData findDoorwayBusiness(@PathVariable long userId);

    /***
     * 条件查询商家
     * @param watchVideos 筛选视频：0否 1是
     * @param name    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findDoorwayBusinessList/{watchVideos}/{name}/{province}/{city}/{district}/{lat}/{lon}/{page}/{count}")
    ReturnData findDoorwayBusinessList(@PathVariable int watchVideos, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 新增商品
     * @param tickets
     * @return
     */
    @PostMapping("addCommodity")
    ReturnData addCommodity(@Valid @RequestBody DoorwayBusinessCommodity tickets, BindingResult bindingResult);

    /***
     * 更新商品
     * @param tickets
     * @return
     */
    @PutMapping("updateCommodity")
    ReturnData updateCommodity(@Valid @RequestBody DoorwayBusinessCommodity tickets, BindingResult bindingResult);

    /**
     * @Description: 删除商品
     * @return:
     */
    @DeleteMapping("delCommodity/{ids}")
    ReturnData delCommodity(@PathVariable String ids);

    /***
     * 查询商品详情
     * @param id
     * @return
     */
    @GetMapping("findCommodity/{id}")
    ReturnData findCommodity(@PathVariable long id);

    /***
     * 分页查询商品列表
     * @param id   商家ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findCommodityList/{id}/{page}/{count}")
    ReturnData findCommodityList(@PathVariable long id, @PathVariable int page, @PathVariable int count);

    /***
     * 新增收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @PostMapping("addBusinessCollect")
    ReturnData addBusinessCollect(@Valid @RequestBody DoorwayBusinessCollection hourlyWorkerCollection, BindingResult bindingResult);

    /***
     * 分页查询收藏列表
     * @param userId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findBusinessCollectList/{userId}/{page}/{count}")
    ReturnData findBusinessCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除收藏
     * @return:
     */
    @DeleteMapping("delBusinessCollect/{ids}")
    ReturnData delBusinessCollect(@PathVariable String ids);
}
