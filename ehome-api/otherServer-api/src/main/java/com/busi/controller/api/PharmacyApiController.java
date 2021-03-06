package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 药店相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-10 15:00:38
 */
public interface PharmacyApiController {

    /***
     * 新增药店
     * @param scenicSpot
     * @return
     */
    @PostMapping("addPharmacy")
    ReturnData addPharmacy(@Valid @RequestBody Pharmacy scenicSpot, BindingResult bindingResult);

    /***
     * 更新药店
     * @param scenicSpot
     * @return
     */
    @PutMapping("changePharmacy")
    ReturnData changePharmacy(@Valid @RequestBody Pharmacy scenicSpot, BindingResult bindingResult);

    /**
     * @Description: 删除药店
     * @return:
     */
    @DeleteMapping("delPharmacy/{userId}/{id}")
    ReturnData delPharmacy(@PathVariable long userId, @PathVariable long id);

    /***
     * 更新药店营业状态
     * @param scenicSpot
     * @return
     */
    @PutMapping("updPharmacyStatus")
    ReturnData updPharmacyStatus(@Valid @RequestBody Pharmacy scenicSpot, BindingResult bindingResult);

    /***
     * 查询药店信息
     * @param userId
     * @return
     */
    @GetMapping("findPharmacy/{userId}")
    ReturnData findPharmacy(@PathVariable long userId);

    /***
     * 查询药店信息
     * @param id
     * @return
     */
    @GetMapping("findPharmacyId/{id}")
    ReturnData findPharmacyId(@PathVariable long id);

    /***
     * 条件查询药店
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
    @GetMapping("findPharmacyList/{watchVideos}/{name}/{province}/{city}/{district}/{lat}/{lon}/{page}/{count}")
    ReturnData findPharmacyList(@PathVariable int watchVideos, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 新增药品
     * @param tickets
     * @return
     */
    @PostMapping("addPharmacyDrugs")
    ReturnData addPharmacyDrugs(@Valid @RequestBody PharmacyDrugs tickets, BindingResult bindingResult);

    /***
     * 更新药品
     * @param tickets
     * @return
     */
    @PutMapping("updatePharmacyDrugs")
    ReturnData updatePharmacyDrugs(@Valid @RequestBody PharmacyDrugs tickets, BindingResult bindingResult);

    /**
     * @Description: 删除药品
     * @return:
     */
    @DeleteMapping("delPharmacyDrugs/{ids}")
    ReturnData delPharmacyDrugs(@PathVariable String ids);

    /***
     * 查询药品详情
     * @param id
     * @return
     */
    @GetMapping("findPharmacyDrugs/{id}")
    ReturnData findPharmacyDrugs(@PathVariable long id);


    /***
     * 分页查询药品列表
     * @param id   药店ID
     * @param natureType   药品性质id  -1不限
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPharmacyDrugsList/{id}/{natureType}/{page}/{count}")
    ReturnData findPharmacyDrugsList(@PathVariable long id, @PathVariable int natureType, @PathVariable int page, @PathVariable int count);

    /***
     * 新增收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @PostMapping("addPharmacyCollect")
    ReturnData addPharmacyCollect(@Valid @RequestBody PharmacyCollection hourlyWorkerCollection, BindingResult bindingResult);

    /***
     * 分页查询收藏列表
     * @param userId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPharmacyCollectList/{userId}/{page}/{count}")
    ReturnData findPharmacyCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除收藏
     * @return:
     */
    @DeleteMapping("delPharmacyCollect/{ids}")
    ReturnData delPharmacyCollect(@PathVariable String ids);

    /***
     * 新增药店数据
     * @param hotelData
     * @return
     */
    @PostMapping("addPharmacyData")
    ReturnData addPharmacyData(@Valid @RequestBody PharmacyData hotelData, BindingResult bindingResult);

    /***
     * 查询药店数据详情
     * @param id
     * @return
     */
    @GetMapping("findPharmacyData/{id}")
    ReturnData findPharmacyData(@PathVariable long id);

    /***
     * 入驻药店
     * @param hotel
     * @return
     */
    @PutMapping("claimPharmacy")
    ReturnData claimPharmacy(@Valid @RequestBody Pharmacy hotel, BindingResult bindingResult);

    /***
     * 查询药店数据列表
     * @param name    药店名称
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPharmacyDataList/{name}/{lat}/{lon}/{page}/{count}")
    ReturnData findPharmacyDataList(@PathVariable String name, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);



}
