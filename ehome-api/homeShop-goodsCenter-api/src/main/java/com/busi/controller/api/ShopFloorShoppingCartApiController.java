package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorShoppingCart;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 楼店商品购物车相关接口
 * author：ZhaoJiaJie
 * create time：2019-12-9 11:15:29
 */
public interface ShopFloorShoppingCartApiController {

    /***
     * 统计购物车商品
     * @param userId
     * @return
     */
    @GetMapping("findFSCartNum/{userId}")
    ReturnData findFSCartNum(@PathVariable long userId);

    /***
     * 加购物车
     * @param shopFloorGoods
     * @return
     */
    @PostMapping("addFShoppingCart")
    ReturnData addFShoppingCart(@Valid @RequestBody ShopFloorShoppingCart shopFloorGoods, BindingResult bindingResult);

    /***
     * 更新购物车
     * @param shopFloorGoods
     * @return
     */
    @PutMapping("changeFShoppingCart")
    ReturnData changeFShoppingCart(@Valid @RequestBody ShopFloorShoppingCart shopFloorGoods, BindingResult bindingResult);

    /**
     * @Description: 删除购物车商品
     * @return:
     */
    @DeleteMapping("delFShoppingCart/{ids}/{userId}")
    ReturnData delFShoppingCart(@PathVariable String ids, @PathVariable long userId);

    /***
     * 查询购物车
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findFShoppingCart/{userId}")
    ReturnData findFShoppingCart(@PathVariable long userId);

    /***
     * 查询已删除商品
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findDeleteGoods/{userId}")
    ReturnData findDeleteGoods(@PathVariable long userId);

}
