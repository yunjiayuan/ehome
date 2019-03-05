package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 小时工主逻辑相关接口
 * author：zhaojiajie
 * create time：2019-1-11 11:22:10
 */
public interface HourlyWorkerApiController {

    /***
     * 新增小时工
     * @param hourlyWorker
     * @return
     */
    @PostMapping("addHourly")
    ReturnData addHourly(@Valid @RequestBody HourlyWorker hourlyWorker, BindingResult bindingResult);

    /***
     * 更新小时工
     * @param hourlyWorker
     * @return
     */
    @PutMapping("updateHourly")
    ReturnData updateHourly(@Valid @RequestBody HourlyWorker hourlyWorker, BindingResult bindingResult);

    /**
     * @Description: 删除小时工
     * @return:
     */
    @DeleteMapping("delHourly/{userId}/{id}")
    ReturnData delHourly(@PathVariable long userId, @PathVariable long id);

    /***
     * 更新小时工营业状态
     * @param hourlyWorker
     * @return
     */
    @PutMapping("updBusinessStatus")
    ReturnData updBusinessStatus(@Valid @RequestBody HourlyWorker hourlyWorker, BindingResult bindingResult);

    /***
     * 查询小时工信息(默认查自己id>0时组合查询)
     * @param id
     * @return
     */
    @GetMapping("getHourly/{id}")
    ReturnData getHourly(@PathVariable long id);

    /***
     * 条件查询小时工
     * @param userId   用户ID
     * @param lat      纬度
     * @param lon      经度
     * @param name     用户名
     * @param page     页码
     * @param count    条数
     * @param sortType 排序类型：默认【0综合排序】   0综合排序  1距离最近  2服务次数最高  3评分最高
     * @return
     */
    @GetMapping("findHourlyList/{userId}/{sortType}/{name}/{lat}/{lon}/{page}/{count}")
    ReturnData findHourlyList(@PathVariable long userId, @PathVariable int sortType, @PathVariable String name, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 检测实名状态
     * @param userId
     * @return
     */
    @GetMapping("realNameStatus/{userId}")
    ReturnData realNameStatus(@PathVariable long userId);

    /***
     * 新增小时工收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @PostMapping("addHourlyCollect")
    ReturnData addHourlyCollect(@Valid @RequestBody HourlyWorkerCollection hourlyWorkerCollection, BindingResult bindingResult);

    /***
     * 分页查询用户收藏列表
     * @param userId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findHourlyCollectList/{userId}/{page}/{count}")
    ReturnData findHourlyCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除收藏
     * @return:
     */
    @DeleteMapping("delHourlyCollect/{ids}")
    ReturnData delHourlyCollect(@PathVariable String ids);

    /***
     * 新增工作类型
     * @param hourlyWorkerType
     * @return
     */
    @PostMapping("addHourlyType")
    ReturnData addHourlyType(@Valid @RequestBody HourlyWorkerType hourlyWorkerType, BindingResult bindingResult);

    /***
     * 更新工作类型
     * @param hourlyWorkerType
     * @return
     */
    @PutMapping("updateHourlyType")
    ReturnData updateHourlyType(@Valid @RequestBody HourlyWorkerType hourlyWorkerType, BindingResult bindingResult);

    /**
     * @Description: 删除工作类型
     * @return:
     */
    @DeleteMapping("delHourlyType/{ids}")
    ReturnData delHourlyType(@PathVariable String ids);

    /***
     * 查询工作类型详情
     * @param id
     * @return
     */
    @GetMapping("getHourlyType/{id}")
    ReturnData getHourlyType(@PathVariable long id);

    /***
     * 分页查询用户工作类型列表
     * @param userId   用户ID
     * @param workerId  店铺ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findHourlyCollectList/{userId}/{workerId}/{page}/{count}")
    ReturnData findHourlyCollectList(@PathVariable long userId, @PathVariable long workerId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增评价点赞
     * @param hourlyWorkerEvaluate
     * @return
     */
    @PostMapping("addHourlyType")
    ReturnData addHourlyType(@Valid @RequestBody HourlyWorkerEvaluate hourlyWorkerEvaluate, BindingResult bindingResult);


}
