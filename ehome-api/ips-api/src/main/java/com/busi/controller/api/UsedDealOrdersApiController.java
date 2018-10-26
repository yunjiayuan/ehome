package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UsedDealExpress;
import com.busi.entity.UsedDealOrders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 二手订单相关接口
 * author：zhaojiajie
 * create time：2018-10-25 09:16:35
 */
public interface UsedDealOrdersApiController {

    /***
     * 新增二手订单
     * @param usedDealOrders
     * @param bindingResult
     * @return
     */
    @PostMapping("addOrders")
    ReturnData addOrders(@Valid @RequestBody UsedDealOrders usedDealOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delOrders/{id}")
    ReturnData delOrders(@PathVariable long id);

    /***
     * 更改发货状态
     * 由未发货改为已发货
     * @param infoId  订单Id
     * @param no  订单编号
     * @param brand  订单物流方式下标
     * @return
     */
    @GetMapping("changeDeliverGoods/{infoId}/{no}/{brand}")
    ReturnData changeDeliverGoods(@PathVariable long infoId, @PathVariable String no, @PathVariable int brand);

    /***
     * 更改收货状态
     * 由未收货改为已收货
     * @param infoId  订单Id
     * @return
     */
    @GetMapping("changeGoodsReceipt/{infoId}")
    ReturnData changeGoodsReceipt(@PathVariable long infoId);

    /***
     * 分页查询二手订单列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity  身份区分：1买家 2商家
     * @param ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时
     * @return
     */
    @GetMapping("findOrdersList/{identity}/{ordersType}/{page}/{count}")
    ReturnData findOrdersList(@PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 延长收货时间
     * @param infoId  订单Id
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @GetMapping("timeExpand/{infoId}/{identity}")
    ReturnData timeExpand(@PathVariable long infoId, @PathVariable int identity);

    /***
     * 取消订单
     * @param infoId  订单Id
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @GetMapping("cancelOrders/{infoId}/{identity}")
    ReturnData cancelOrders(@PathVariable long infoId, @PathVariable int identity);

    /***
     * 查看订单详情
     * @param infoId  订单Id
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @GetMapping("ordersDetails/{infoId}/{identity}")
    ReturnData ordersDetails(@PathVariable long infoId, @PathVariable int identity);

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @GetMapping("findBabyCount/{identity}")
    ReturnData findBabyCount(@PathVariable int identity);

    /***
     * 新增二手快递
     * @param usedDealExpress
     * @param bindingResult
     * @return
     */
    @PostMapping("addExpress")
    ReturnData addExpress(@Valid @RequestBody UsedDealExpress usedDealExpress, BindingResult bindingResult);

    /**
     * @Description: 更新二手快递
     * @Param: usedDealExpress
     * @return:
     */
    @PutMapping("updateExpress")
    ReturnData updateExpress(@Valid @RequestBody UsedDealExpress usedDealExpress, BindingResult bindingResult);

    /***
     * 删除快递
     * @param id 快递ID
     * @return
     */
    @DeleteMapping("delExpress/{id}")
    ReturnData delExpress(@PathVariable long id);

    /***
     * 分页查询快递列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @return
     */
    @GetMapping("findExpress/{userId}/{page}/{count}")
    ReturnData findExpress(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 查看订单物流详情
     * @param infoId  物流Id
     * @return
     */
    @GetMapping("logisticsDetails/{infoId}")
    ReturnData logisticsDetails(@PathVariable long infoId);

}
