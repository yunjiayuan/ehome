package com.busi.controller.api;

import com.busi.entity.KitchenBookedOrders;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 厨房订座订单相关接口
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 16:10
 */
public interface KitchenBookedOrdersApiController {

    /***
     * 新增订单
     * @param kitchenBookedOrders
     * @return
     */
    @PostMapping("addBookedOrder")
    ReturnData addBookedOrder(@Valid @RequestBody KitchenBookedOrders kitchenBookedOrders, BindingResult bindingResult);

    /***
     * 更新订单（加菜）
     * @param kitchenBookedOrders
     * @return
     */
    @PutMapping("addToFood")
    ReturnData addToFood(@Valid @RequestBody KitchenBookedOrders kitchenBookedOrders, BindingResult bindingResult);


    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delBookedOrder/{id}")
    ReturnData delBookedOrder(@PathVariable long id);

    /***
     * 更改单子状态
     * 由未接单改为已接单
     * @param id  订单Id
     * @return
     */
    @GetMapping("receiptBooked/{id}")
    ReturnData receiptBooked(@PathVariable long id);

    /***
     * 更改单子状态
     * 由已接单改为菜已上桌
     * @param id  订单Id
     * @return
     */
    @GetMapping("upperTable/{id}")
    ReturnData upperTable(@PathVariable long id);

    /***
     * 更改单子状态
     * 由菜已上桌改为进餐中
     * @param id  订单Id
     * @return
     */
    @GetMapping("dining/{id}")
    ReturnData dining(@PathVariable long id);

    /***
     * 更改单子状态
     * 由进餐中改为完成
     * @param id  订单Id
     * @return
     */
    @GetMapping("completeBooked/{id}")
    ReturnData completeBooked(@PathVariable long id);

    /***
     * 更改单子状态
     * 由完成改为已清桌
     * @param id  订单Id
     * @return
     */
    @GetMapping("clearTable/{id}")
    ReturnData clearTable(@PathVariable long id);

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
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @GetMapping("countBookedOrders/{identity}")
    ReturnData countBookedOrders(@PathVariable int identity);

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2已接单,3已完成  4卖家取消订单 5用户取消订单 6付款超时 7接单超时
     * @return
     */
    @GetMapping("findBookedOrderList/{userId}/{identity}/{ordersType}/{page}/{count}")
    ReturnData findBookedOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

}
