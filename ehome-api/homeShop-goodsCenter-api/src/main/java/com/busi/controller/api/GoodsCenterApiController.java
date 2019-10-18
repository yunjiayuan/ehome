package com.busi.controller.api;

import com.busi.entity.GoodsDescribe;
import com.busi.entity.GoodsSort;
import com.busi.entity.HomeShopGoods;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商品信息相关接口 如：发布商品 管理商品 商品上下架等等
 * author：ZhaoJiaJie
 * create time：2019-4-17 15:31:17
 */
public interface GoodsCenterApiController {

    /***
     * 发布商品
     * @param homeShopGoods
     * @return
     */
    @PostMapping("addShopGoods")
    ReturnData addShopGoods(@Valid @RequestBody HomeShopGoods homeShopGoods, BindingResult bindingResult);

    /***
     * 更新商品
     * @param homeShopGoods
     * @return
     */
    @PutMapping("changeShopGoods")
    ReturnData changeShopGoods(@Valid @RequestBody HomeShopGoods homeShopGoods, BindingResult bindingResult);

    /**
     * @Description: 删除商品
     * @return:
     */
    @DeleteMapping("delShopGoods/{ids}/{userId}")
    ReturnData delShopGoods(@PathVariable String ids, @PathVariable long userId);

    /***
     * 批量上下架商品
     * @param ids
     * @return
     */
    @GetMapping("changeShopGoods/{ids}/{userId}/{sellType}")
    ReturnData changeShopGoods(@PathVariable String ids, @PathVariable long userId, @PathVariable int sellType);

    /***
     * 查询详情
     * @param id
     * @return
     */
    @GetMapping("getShopGoods/{id}")
    ReturnData getShopGoods(@PathVariable long id);

    /***
     * 分页查询
     * @param sort  排序条件:0出售中，1仓库中，2已预约
     * @param shopId  店铺ID
     * @param stock  库存：0倒序 1正序
     * @param time  时间：0倒序 1正序
     * @param goodsSort  分类
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findGoodsList/{sort}/{shopId}/{stock}/{time}/{goodsSort}/{page}/{count}")
    ReturnData findGoodsList(@PathVariable int sort, @PathVariable long shopId, @PathVariable int stock, @PathVariable int time, @PathVariable long goodsSort, @PathVariable int page, @PathVariable int count);

    /***
     * 新增分类
     * @param goodsSort
     * @return
     */
    @PostMapping("addGoodsSort")
    ReturnData addGoodsSort(@Valid @RequestBody GoodsSort goodsSort, BindingResult bindingResult);

    /***
     * 修改分类
     * @param goodsSort
     * @return
     */
    @PutMapping("changeGoodsSort")
    ReturnData changeGoodsSort(@Valid @RequestBody GoodsSort goodsSort, BindingResult bindingResult);


    /***
     * 批量修改商品所属分类
     * @param ids
     * @return
     */
    @GetMapping("editGoodsSort/{ids}/{sortId}/{sortName}")
    ReturnData editGoodsSort(@PathVariable String ids, @PathVariable long sortId, @PathVariable String sortName);

    /**
     * @Description: 删除分类
     * @return:
     */
    @DeleteMapping("delGoodsSort/{ids}")
    ReturnData delGoodsSort(@PathVariable String ids);

    /***
     * 查询分类列表
     * @param id  店铺
     * @param find  0默认所有 1一级分类 2子级分类
     * @param sortId  分类ID(仅查询子级分类有效)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("getGoodsSortList/{id}/{find}/{sortId}/{page}/{count}")
    ReturnData getGoodsSortList(@PathVariable long id, @PathVariable int find, @PathVariable int sortId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增商品描述
     * @param goodsDescribe
     * @return
     */
    @PostMapping("addGoodsDescribe")
    ReturnData addGoodsDescribe(@Valid @RequestBody GoodsDescribe goodsDescribe, BindingResult bindingResult);

    /***
     * 更新商品描述
     * @param goodsDescribe
     * @return
     */
    @PutMapping("changeGoodsDescribe")
    ReturnData changeGoodsDescribe(@Valid @RequestBody GoodsDescribe goodsDescribe, BindingResult bindingResult);


    /**
     * @Description: 删除商品描述
     * @return:
     */
    @DeleteMapping("delGoodsDescribe/{id}/{userId}")
    ReturnData delGoodsDescribe(@PathVariable long id, @PathVariable long userId);

    /***
     * 查询商品描述
     * @param id
     * @return
     */
    @GetMapping("getGoodsDescribe/{id}")
    ReturnData getGoodsDescribe(@PathVariable long id);

}
