package com.busi.controller.api;

import com.busi.entity.KitchenEvaluate;
import com.busi.entity.KitchenOrders;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 厨房订单相关接口
 * author：zhaojiajie
 * create time：2019-3-1 10:35:22
 */
public interface KitchenOrdersApiController {

    /***
     * 新增订单
     * @param kitchenOrders
     * @return
     */
    @PostMapping("addKitchenOrders")
    ReturnData addKitchenOrders(@Valid @RequestBody KitchenOrders kitchenOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delKitchenOrders/{id}")
    ReturnData delKitchenOrders(@PathVariable long id);

    /***
     * 更改单子状态
     * 由未接单改为制作中【已接单】
     * @param id  订单Id
     * @return
     */
    @GetMapping("kitchenReceipt/{id}")
    ReturnData kitchenReceipt(@PathVariable long id);

    /***
     * 更改单子状态
     * 由制作中改为配送中
     * @param id  订单Id
     * @return
     */
    @GetMapping("kitchenDelivery/{id}")
    ReturnData kitchenDelivery(@PathVariable long id);

    /***
     * 更改单子状态
     * 由配送中改为已卖出
     * @param id  订单Id
     * @return
     */
    @GetMapping("kitchenSell/{id}")
    ReturnData kitchenSell(@PathVariable long id);

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2制作中(已接单未发货),3配送(已发货未收货),4已卖出(已收货未评价),  5卖家取消订单 6付款超时 7接单超时 8发货超时 9用户取消订单 10 已评价
     * @return
     */
    @GetMapping("findKitchenOrdersList/{userId}/{identity}/{ordersType}/page}/{count}")
    ReturnData findKitchenOrdersList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 查看订单详情
     * @param id  订单Id
     * @return
     */
    @GetMapping("findKitchenOrders/{id}")
    ReturnData findKitchenOrders(@PathVariable long id);

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    @GetMapping("findKitchenOrders/{identity}")
    ReturnData findKitchenOrders(@PathVariable int identity);

    /***
     * 取消订单（更新订单类型）
     * @param identity
     * @return
     */
    @GetMapping("cancelKitchenOrders/{identity}/{id}")
    ReturnData cancelKitchenOrders(@PathVariable int identity, @PathVariable long id);

    /***
     * 菜品点赞
     * @param infoId 订单
     * @return
     */
    @GetMapping("addDishesLike/{infoId}/{dishesIds}")
    ReturnData addDishesLike(@PathVariable long infoId, @PathVariable String dishesIds);

    /***
     * 新增评价
     * @param kitchenEvaluate
     * @return
     */
    @PostMapping("addKitchenEvaluate")
    ReturnData addKitchenEvaluate(@Valid @RequestBody KitchenEvaluate kitchenEvaluate, BindingResult bindingResult);

    /***
     * 删除评价
     * @param id 评价ID
     * @return
     */
    @DeleteMapping("delKitchenEvaluate/{id}")
    ReturnData delKitchenEvaluate(@PathVariable long id);

}
