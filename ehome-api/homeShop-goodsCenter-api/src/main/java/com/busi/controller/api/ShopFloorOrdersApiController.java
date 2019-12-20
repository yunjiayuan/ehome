package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorOrders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 楼店订单相关接口
 * author：ZhaoJiaJie
 * create time：2019-12-17 14:47:43
 */
public interface ShopFloorOrdersApiController {

    /***
     * 新增订单
     * @param shopFloorOrders
     * @param bindingResult
     * @return
     */
    @PostMapping("addSForders")
    ReturnData addSForders(@Valid @RequestBody ShopFloorOrders shopFloorOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delSForders/{id}")
    ReturnData delSForders(@PathVariable long id);

    /***
     * 更改发货状态
     * 由未发货改为已发货
     * @param id  订单Id
     * @return
     */
    @GetMapping("changeSFdeliver/{id}")
    ReturnData changeSFdeliver(@PathVariable long id);

    /***
     * 更改收货状态
     * 由未收货改为已收货
     * @param id  订单Id
     * @return
     */
    @GetMapping("changeSFreceipt/{id}")
    ReturnData changeSFreceipt(@PathVariable long id);

    /***
     * 分页查询订单列表
     * @param identity  身份区分：1买家 2商家
     * @param ordersType 订单类型: 0全部 1待付款,2待发货(已付款),3已发货（待收货）, 4已收货（待评价）  5已评价  6付款超时  7发货超时, 8取消订单
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findSFordersList/{identity}/{ordersType}/{page}/{count}")
    ReturnData findSFordersList(@PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 取消订单
     * @param id  订单Id
     * @return
     */
    @GetMapping("cancelSForders/{id}")
    ReturnData cancelSForders(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("orderDetails/{no}")
    ReturnData orderDetails(@PathVariable String no);

    /***
     * 统计各类订单数量
     * @return
     */
    @GetMapping("findSFordersCount/{identity}")
    ReturnData findSFordersCount(@PathVariable int identity);
}
