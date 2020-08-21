package com.busi.controller.api;

import com.busi.entity.HotelTourismBookedOrders;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 酒店景区订座订单相关接口
 * @author: ZHaoJiaJie
 * @create: 2020-08-20 14:57:48
 */
public interface HotelTourismBookedOrdersApiController {

    /***
     * 新增订单
     * @param kitchenHotelTourismOrders
     * @return
     */
    @PostMapping("addHotelTourismOrder")
    ReturnData addHotelTourismOrder(@Valid @RequestBody HotelTourismBookedOrders kitchenHotelTourismOrders, BindingResult bindingResult);

    /***
     * 更新订单（加菜）
     * @param kitchenHotelTourismOrders
     * @return
     */
    @PutMapping("addHotelTourismToFood")
    ReturnData addHotelTourismToFood(@Valid @RequestBody HotelTourismBookedOrders kitchenHotelTourismOrders, BindingResult bindingResult);


    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delHotelTourismOrder/{id}")
    ReturnData delHotelTourismOrder(@PathVariable long id);

    /***
     * 更改单子状态
     * 由未接单改为已接单
     * @param id  订单Id
     * @return
     */
    @GetMapping("receiptHotelTourism/{id}")
    ReturnData receiptHotelTourism(@PathVariable long id);

    /***
     * 更改单子状态
     * 由已接单改为菜已上桌
     * @param id  订单Id
     * @return
     */
    @GetMapping("upperHotelTourismTable/{id}")
    ReturnData upperHotelTourismTable(@PathVariable long id);

    /***
     * 更改单子状态
     * 由进餐中改为完成
     * @param id  订单Id
     * @return
     */
    @GetMapping("completeHotelTourism/{id}")
    ReturnData completeHotelTourism(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("findHotelTourismOrder/{no}")
    ReturnData findHotelTourismOrder(@PathVariable String no);

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @GetMapping("cancelHotelTourismOrders/{id}")
    ReturnData cancelHotelTourismOrders(@PathVariable long id);

    /***
     * 统计各类订单数量
     * @param type     0酒店 1景区
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @GetMapping("countHotelTourismOrders/{type}/{identity}")
    ReturnData countHotelTourismOrders(@PathVariable int type, @PathVariable int identity);

    /***
     * 订单管理条件查询
     * @param type     0酒店 1景区
     * @param identity     身份区分：1买家 2商家
     * @param ordersType   订单类型:  订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2已接单,3已完成  4卖家取消订单 5用户取消订单 6付款超时 7接单超时
     * @param count        每页的显示条数
     * @param page         当前查询数据的页码
     * @return
     */
    @GetMapping("findHotelTourismOrderList/{type}/{userId}/{identity}/{ordersType}/{page}/{count}")
    ReturnData findHotelTourismOrderList(@PathVariable int type, @PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

}
