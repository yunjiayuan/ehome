package com.busi.controller.api;

import com.busi.entity.PharmacyOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 家门口药店订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-10 17:36:19
 */
public interface PharmacyOrderApiController {
    /***
     * 新增订单
     * @param pharmacyOrder
     * @return
     */
    @PostMapping("addPharmacyOrder")
    ReturnData addPharmacyOrder(@Valid @RequestBody PharmacyOrder pharmacyOrder, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delPharmacyOrder/{id}")
    ReturnData delPharmacyOrder(@PathVariable long id);

    /***
     * 更改接单状态
     * @param id  订单Id
     * @return
     */
    @GetMapping("receivingPharmacy/{id}")
    ReturnData receivingPharmacy(@PathVariable long id);

    /***
     * 更改配送状态
     * @param id  订单Id
     * @return
     */
    @GetMapping("distributionPharmacy/{id}")
    ReturnData distributionPharmacy(@PathVariable long id);

    /***
     * 更改验票状态
     * @param id  订单Id
     * @param voucherCode  凭证码
     * @return
     */
    @GetMapping("receiptPharmacy/{id}/{voucherCode}")
    ReturnData receiptPharmacy(@PathVariable long id, @PathVariable String voucherCode);

    /***
     * 完成订单
     * @param id  订单Id
     * @return
     */
    @GetMapping("completePharmacy/{id}")
    ReturnData completePharmacy(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("findPharmacyOrder/{no}")
    ReturnData findPharmacyOrder(@PathVariable String no);

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @GetMapping("cancelPharmacyOrders/{id}")
    ReturnData cancelPharmacyOrders(@PathVariable long id);

    /***
     * 订单管理条件查询
     * @param identity     身份区分：1买家 2商家
     * @param ordersType   订单类型:  订单类型:  0未付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成  4卖家取消订单 5用户取消订单
     * @param page         当前查询数据的页码
     * @param count        每页的显示条数
     * @return
     */
    @GetMapping("findPharmacyOrderList/{userId}/{identity}/{ordersType}/{page}/{count}")
    ReturnData findPharmacyOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

}
