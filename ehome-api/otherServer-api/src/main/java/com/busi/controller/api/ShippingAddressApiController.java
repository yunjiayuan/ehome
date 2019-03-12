package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShippingAddress;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 收货地址相关接口
 * author：zhaojiajie
 * create time：2018-9-20 12:07:35
 */
public interface ShippingAddressApiController {

    /***
     * 新增收货地址
     * @param shippingAddress
     * @param bindingResult
     * @return
     */
    @PostMapping("addAddress")
    ReturnData addAddress(@Valid @RequestBody ShippingAddress shippingAddress, BindingResult bindingResult);

    /**
     * @Description: 删除收货地址
     * @return:
     */
    @DeleteMapping("delAddress/{id}/{userId}")
    ReturnData delAddress(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新收货地址
     * @Param: shippingAddress
     * @return:
     */
    @PutMapping("updateAddress")
    ReturnData updateAddress(@Valid @RequestBody ShippingAddress shippingAddress, BindingResult bindingResult);

    /**
     * 查询收货地址详情
     *
     * @param id
     * @return
     */
    @GetMapping("getAddress/{id}")
    ReturnData getAddress(@PathVariable long id);

    /**
     * 查询默认收货地址
     *
     * @return
     */
    @GetMapping("getDefault")
    ReturnData getDefault();

    /***
     * 分页查询收货地址
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findAddressList/{page}/{count}")
    ReturnData findAddressList(@PathVariable int page, @PathVariable int count);

    /**
     * @Description: 设置默认收货地址
     * @Param: id  ID
     * @return:
     */
    @GetMapping("setDefault/{id}")
    ReturnData setDefault(@PathVariable long id);
}
