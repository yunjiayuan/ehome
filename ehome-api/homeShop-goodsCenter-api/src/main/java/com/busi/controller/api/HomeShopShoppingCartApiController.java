package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.HomeShopShoppingCart;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 二货商城商品购物车相关接口
 * author：ZhaoJiaJie
 * create time：2020-07-13 13:25:13
 */
public interface HomeShopShoppingCartApiController {

    /***
     * 统计购物车商品
     * @param userId
     * @return
     */
    @GetMapping("findHSCartNum/{userId}")
    ReturnData findHSCartNum(@PathVariable long userId);

    /***
     * 加购物车
     * @param shopFloorGoods
     * @return
     */
    @PostMapping("addHShoppingCart")
    ReturnData addHShoppingCart(@Valid @RequestBody HomeShopShoppingCart shopFloorGoods, BindingResult bindingResult);

    /***
     * 更新购物车
     * @param shopFloorGoods
     * @return
     */
    @PutMapping("changeHShoppingCart")
    ReturnData changeHShoppingCart(@Valid @RequestBody HomeShopShoppingCart shopFloorGoods, BindingResult bindingResult);

    /**
     * @Description: 删除购物车商品
     * @return:
     */
    @DeleteMapping("delHShoppingCart/{ids}/{userId}")
    ReturnData delHShoppingCart(@PathVariable String ids, @PathVariable long userId);

    /***
     * 查询购物车
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findHShoppingCart/{userId}")
    ReturnData findHShoppingCart(@PathVariable long userId);

    /***
     * 查询已删除商品
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findHSDeleteGoods/{userId}")
    ReturnData findHSDeleteGoods(@PathVariable long userId);

}
