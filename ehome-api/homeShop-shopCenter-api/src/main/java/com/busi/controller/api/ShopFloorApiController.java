package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloor;
import com.busi.entity.ShopFloorBondOrders;
import com.busi.entity.YongHuiGoodsSort;
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

    /***
     * 查询小区
     * @param villageOnly
     * @return
     */
    @GetMapping("findVillage/{villageOnly}")
    ReturnData findVillage(@PathVariable String villageOnly);

    /***
     * 新增永辉分类
     * @param yongHuiGoodsSort
     * @return
     */
    @PostMapping("addYHSort")
    ReturnData addYHSort(@Valid @RequestBody YongHuiGoodsSort yongHuiGoodsSort, BindingResult bindingResult);

    /***
     * 更新永辉分类
     * @param yongHuiGoodsSort
     * @return
     */
    @PutMapping("changeYHSort")
    ReturnData changeYHSort(@Valid @RequestBody YongHuiGoodsSort yongHuiGoodsSort, BindingResult bindingResult);

    /***
     * 查询永辉分类
     * @param levelOne 商品1级分类
     * @param levelTwo 商品2级分类
     * @param letter 商品分类首字母
     * @return
     */
    @GetMapping("findYHSort/{levelOne}/{levelTwo}/{letter}")
    ReturnData findYHSort(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable String letter);

    /**
     * @Description: 删除永辉分类
     * @return:
     */
    @DeleteMapping("delYHSort/{ids}")
    ReturnData delYHSort(@PathVariable String ids);
}
