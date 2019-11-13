package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloor;
import com.busi.entity.ShopFloorBondOrders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 楼店信息相关接口 如：创建店铺 修改店铺信息 更改店铺状态等
 * author：ZJJ
 * create time：2019-11-12 16:09:54
 */
public interface ShopFloorApiController {

    /***
     * 新增店铺
     * @param homeShopCenter
     * @return
     */
    @PostMapping("addShopFloor")
    ReturnData addShopFloor(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult);

    /***
     * 更新店铺
     * @param homeShopCenter
     * @return
     */
    @PutMapping("changeShopFloor")
    ReturnData changeShopFloor(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult);

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @PutMapping("updShopFloorStatus")
    ReturnData updShopFloorStatus(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult);

    /***
     * 查询店铺信息
     * @param userId
     * @return
     */
    @GetMapping("findShopFloor/{userId}")
    ReturnData findShopFloor(@PathVariable long userId);

    /***
     * 查询店铺状态
     * @param userId
     * @return
     */
    @GetMapping("findFloorState/{userId}")
    ReturnData findFloorState(@PathVariable long userId);

    /***
     * 新增订单
     * @param shopFloorBondOrders
     * @param bindingResult
     * @return
     */
    @PostMapping("addBondOrder")
    ReturnData addBondOrder(@Valid @RequestBody ShopFloorBondOrders shopFloorBondOrders, BindingResult bindingResult);

}
