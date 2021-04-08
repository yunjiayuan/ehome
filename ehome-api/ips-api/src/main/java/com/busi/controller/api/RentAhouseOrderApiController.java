package com.busi.controller.api;

import com.busi.entity.RentAhouseOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 租房买房订单相关接口
 * author：zhaojiajie
 * create time：2021-03-29 17:45:18
 */
public interface RentAhouseOrderApiController {

    /***
     * 新增订单
     * @param usedDealOrders
     * @param bindingResult
     * @return
     */
    @PostMapping("addHouseOrders")
    ReturnData addHouseOrders(@Valid @RequestBody RentAhouseOrder usedDealOrders, BindingResult bindingResult);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("houseOrdersDetails/{no}")
    ReturnData houseOrdersDetails(@PathVariable String no);

    /***
     * 分页查询订单列表
     * @param type  房屋类型: -1默认全部 0购房  1租房
     * @param ordersType 订单类型:  type=0时：0购房  1出售  type=1时：0租房  1出租
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findHouseOrdersList/{type}/{ordersType}/{page}/{count}")
    ReturnData findHouseOrdersList(@PathVariable int type, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

}
