package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorGoods;
import com.busi.entity.ShopFloorGoodsDescribe;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 楼店商品信息相关接口 如：发布商品 管理商品 商品上下架等等
 * author：ZhaoJiaJie
 * create time：2019-11-19 13:24:33
 */
public interface ShopFloorGoodsApiController {

    /***
     * 发布商品
     * @param shopFloorGoods
     * @return
     */
    @PostMapping("addFloorGoods")
    ReturnData addFloorGoods(@Valid @RequestBody ShopFloorGoods shopFloorGoods, BindingResult bindingResult);

    /***
     * 更新商品
     * @param shopFloorGoods
     * @return
     */
    @PutMapping("changeFloorGoods")
    ReturnData changeFloorGoods(@Valid @RequestBody ShopFloorGoods shopFloorGoods, BindingResult bindingResult);

    /**
     * @Description: 删除商品
     * @return:
     */
    @DeleteMapping("delFloorGoods/{ids}/{userId}")
    ReturnData delFloorGoods(@PathVariable String ids, @PathVariable long userId);

    /***
     * 批量上下架商品
     * @param ids
     * @return
     */
    @GetMapping("changeFloorGoods/{ids}/{userId}/{sellType}")
    ReturnData changeFloorGoods(@PathVariable String ids, @PathVariable long userId, @PathVariable int sellType);

    /***
     * 查询详情
     * @param id
     * @return
     */
    @GetMapping("getFloorGoods/{id}")
    ReturnData getFloorGoods(@PathVariable long id);

    /***
     * 分页查询商品
     * @param sort  排序条件:0默认销量排序，1最新发布 价格最低，3价格最高 4有货 5没货
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param levelOne  一级分类:默认值为0,-2为不限
     * @param levelTwo  二级分类:默认值为0,-2为不限
     * @param levelThree  三级分类:默认值为0,-2为不限
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findFloorGoodsList/{sort}/{minPrice}/{maxPrice}/{levelOne}/{levelTwo}/{levelThree}/{page}/{count}")
    ReturnData findFloorGoodsList(@PathVariable int sort, @PathVariable int minPrice, @PathVariable int maxPrice, @PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable int page, @PathVariable int count);

    /***
     * 新增商品描述
     * @param goodsDescribe
     * @return
     */
    @PostMapping("addFGDescribe")
    ReturnData addFGDescribe(@Valid @RequestBody ShopFloorGoodsDescribe goodsDescribe, BindingResult bindingResult);

    /***
     * 更新商品描述
     * @param goodsDescribe
     * @return
     */
    @PutMapping("changeFGDescribe")
    ReturnData changeFGDescribe(@Valid @RequestBody ShopFloorGoodsDescribe goodsDescribe, BindingResult bindingResult);


    /**
     * @Description: 删除商品描述
     * @return:
     */
    @DeleteMapping("delFGDescribe/{id}/{userId}")
    ReturnData delFGDescribe(@PathVariable long id, @PathVariable long userId);

    /***
     * 查询商品描述
     * @param id
     * @return
     */
    @GetMapping("getFGDescribe/{id}")
    ReturnData getFGDescribe(@PathVariable long id);
}