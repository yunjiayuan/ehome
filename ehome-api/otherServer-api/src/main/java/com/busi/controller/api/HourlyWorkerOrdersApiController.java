package com.busi.controller.api;

import com.busi.entity.HourlyWorkerOrders;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 小时工订单主逻辑相关接口
 * author：zhaojiajie
 * create time：2019-1-11 11:22:10
 */
public interface HourlyWorkerOrdersApiController {

    /***
     * 新增小时工订单
     * @param hourlyWorkerOrders
     * @return
     */
    @PostMapping("addHourlyOrders")
    ReturnData addHourlyOrders(@Valid @RequestBody HourlyWorkerOrders hourlyWorkerOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delHourlyOrders/{id}")
    ReturnData delHourlyOrders(@PathVariable long id);

    /***
     * 更改单子状态
     * 由未接单改为已接单
     * @param id  订单Id
     * @return
     */
    @GetMapping("hourlyReceipt/{id}")
    ReturnData hourlyReceipt(@PathVariable long id);

    /***
     * 更改单子状态
     * 由服务中改为已完成
     * @param id  订单Id
     * @return
     */
    @GetMapping("hourlyComplete/{id}")
    ReturnData hourlyComplete(@PathVariable long id);

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  0已下单未付款  1已接单未完成  ,2已完成(已完成未评价),  3接单超时  4商家取消订单 5用户取消订单  6已评价
     * @return
     */
    @GetMapping("findHourlyOrdersList/{userId}/{identity}/{ordersType}/page}/{count}")
    ReturnData findHourlyOrdersList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 查看订单详情
     * @param id  订单Id
     * @return
     */
    @GetMapping("findHourlyOrders/{id}")
    ReturnData findHourlyOrders(@PathVariable long id);

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @GetMapping("findHourlyOrders/{identity}")
    ReturnData findHourlyOrders(@PathVariable int identity);

    /***
     * 取消订单（更新订单类型）
     * @param hourlyWorkerOrders
     * @return
     */
    @PutMapping("updHourlyOrdersType")
    ReturnData updHourlyOrdersType(@Valid @RequestBody HourlyWorkerOrders hourlyWorkerOrders, BindingResult bindingResult);
}
