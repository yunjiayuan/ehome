package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorMasterOrders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 楼店订单相关接口
 * author：ZhaoJiaJie
 * create time：2020-1-9 15:33:45
 */
public interface ShopFloorMasterOrdersApiController {

    /***
     * 新增订单
     * @param shopFloorOrders
     * @param bindingResult
     * @return
     */
    @PostMapping("addSFMorders")
    ReturnData addSFMorders(@Valid @RequestBody ShopFloorMasterOrders shopFloorOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delSFMorders/{id}")
    ReturnData delSFMorders(@PathVariable long id);

    /***
     * 更改发货状态
     * 由未发货改为已发货
     * @param id  订单Id
     * @return
     */
    @GetMapping("changeSFMdeliver/{id}")
    ReturnData changeSFMdeliver(@PathVariable long id);

    /***
     * 更改收货状态
     * 由未收货改为已收货
     * @param id  订单Id
     * @return
     */
    @GetMapping("changeSFMreceipt/{id}")
    ReturnData changeSFMreceipt(@PathVariable long id);

    /***
     * 分页查询订单列表
     * @param ordersType 订单类型: 0全部 1待付款,2待发货(已付款),3已发货（待收货）, 4已收货（待评价）  5已评价  6付款超时  7发货超时, 8取消订单
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findSFMordersList/{ordersType}/{page}/{count}")
    ReturnData findSFMordersList(@PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 取消订单
     * @param id  订单Id
     * @return
     */
    @GetMapping("cancelSFMorders/{id}")
    ReturnData cancelSFMorders(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("orderMdetails/{no}")
    ReturnData orderMdetails(@PathVariable String no);

    /***
     * 统计各类订单数量
     * @return
     */
    @GetMapping("findSFMordersCount")
    ReturnData findSFMordersCount();
}
