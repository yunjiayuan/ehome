package com.busi.controller.api;

import com.busi.entity.ScenicSpotOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 家门口旅游订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 13:19:48
 */
public interface TravelOrderApiController {

    /***
     * 新增订单
     * @param kitchenBookedOrders
     * @return
     */
    @PostMapping("addBookedOrder")
    ReturnData addBookedOrder(@Valid @RequestBody ScenicSpotOrder kitchenBookedOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delBookedOrder/{id}")
    ReturnData delBookedOrder(@PathVariable long id);

    /***
     * 更改订单状态
     * 由未验票改为已验票
     * @param id  订单Id
     * @return
     */
    @GetMapping("receiptBooked/{id}")
    ReturnData receiptBooked(@PathVariable long id);

    /***
     * 更改订单状态
     * 由已验票改为已完成
     * @param id  订单Id
     * @return
     */
    @GetMapping("upperTable/{id}")
    ReturnData upperTable(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("findBookedOrder/{no}")
    ReturnData findBookedOrder(@PathVariable String no);

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @GetMapping("cancelBookedOrders/{id}")
    ReturnData cancelBookedOrders(@PathVariable long id);

    /***
     * 订单管理条件查询
     * @param identity     身份区分：1买家 2商家
     * @param ordersType   订单类型:  订单类型:  0未付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成  4卖家取消订单 5用户取消订单
     * @param page         当前查询数据的页码
     * @param count        每页的显示条数
     * @return
     */
    @GetMapping("findBookedOrderList/{userId}/{identity}/{ordersType}/{page}/{count}")
    ReturnData findBookedOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

}
