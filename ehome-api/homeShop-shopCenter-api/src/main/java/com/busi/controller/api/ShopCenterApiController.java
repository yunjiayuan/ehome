package com.busi.controller.api;

import com.busi.entity.GoodsCategory;
import com.busi.entity.HomeShopCenter;
import com.busi.entity.HomeShopPersonalData;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 店铺信息相关接口 如：创建店铺 修改店铺信息 更改店铺状态等
 * author：ZHJJ
 * create time：2019-5-10 15:15:44
 */
public interface ShopCenterApiController {

    /***
     * 新增店铺
     * @param homeShopCenter
     * @return
     */
    @PostMapping("addHomeShop")
    ReturnData addHomeShop(@Valid @RequestBody HomeShopCenter homeShopCenter, BindingResult bindingResult);

    /***
     * 更新店铺
     * @param homeShopCenter
     * @return
     */
    @PutMapping("changeHomeShop")
    ReturnData changeHomeShop(@Valid @RequestBody HomeShopCenter homeShopCenter, BindingResult bindingResult);

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @PutMapping("updHomeShopStatus")
    ReturnData updHomeShopStatus(@Valid @RequestBody HomeShopCenter homeShopCenter, BindingResult bindingResult);

    /***
     * 查询店铺信息
     * @param userId
     * @return
     */
    @GetMapping("findHomeShop/{userId}")
    ReturnData findHomeShop(@PathVariable long userId);

    /***
     * 查询店铺状态
     * @param userId
     * @return
     */
    @GetMapping("findHomeState/{userId}")
    ReturnData findHomeState(@PathVariable long userId);

    /***
     * 新增个人信息
     * @param homeShopPersonalData
     * @return
     */
    @PostMapping("addPersonalData")
    ReturnData addPersonalData(@Valid @RequestBody HomeShopPersonalData homeShopPersonalData, BindingResult bindingResult);

    /***
     * 更新个人信息
     * @param homeShopPersonalData
     * @return
     */
    @PutMapping("changePersonalData")
    ReturnData changePersonalData(@Valid @RequestBody HomeShopPersonalData homeShopPersonalData, BindingResult bindingResult);

    /***
     * 查询个人信息
     * @param userId
     * @return
     */
    @GetMapping("findPersonalData/{userId}")
    ReturnData findPersonalData(@PathVariable long userId);

    /***
     * 验证手机验证码
     * @param userId
     * @return
     */
    @GetMapping("verificationCode/{userId}/{phone}/{code}")
    ReturnData verificationCode(@PathVariable long userId, @PathVariable String phone, @PathVariable String code);

    /***
     * 查询个人信息认证状态
     * @param userId
     * @return
     */
    @GetMapping("findPersonalState/{userId}")
    ReturnData findPersonalState(@PathVariable long userId);

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类:0图书、音像、电子书刊  1手机、数码  2家用电器  3家居家装  4电脑、办公  5厨具  6个护化妆  7服饰内衣  8钟表  9鞋靴  10母婴  11礼品箱包  12食品饮料、保健食品  13珠宝  14汽车用品  15运动健康  16玩具乐器  17彩票、旅行、充值、票务
     * @param levelTwo 商品2级分类
     * @param levelThree 商品3级分类
     * @param levelFour 商品4级分类
     * @param levelFive 商品5级分类
     * @param letter 商品分类首字母
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findGoodsCategory/{levelOne}/{levelTwo}/{levelThree}/{levelFour}/{levelFive}/{letter}/{page}/{count}")
    ReturnData findGoodsCategory(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable int levelFour, @PathVariable int levelFive, @PathVariable String letter, @PathVariable int page, @PathVariable int count);

    /***
     * 新增商品分类
     * @param homeShopCenter
     * @return
     */
    @PostMapping("addGoodsCategory")
    ReturnData addGoodsCategory(@Valid @RequestBody GoodsCategory homeShopCenter, BindingResult bindingResult);

    /***
     * 更新商品分类
     * @param homeShopCenter
     * @return
     */
    @PutMapping("changeGoodsCategory")
    ReturnData changeGoodsCategory(@Valid @RequestBody GoodsCategory homeShopCenter, BindingResult bindingResult);

    /**
     * @Description: 删除商品分类
     * @return:
     */
    @DeleteMapping("delGoodsCategory/{ids}")
    ReturnData delGoodsCategory(@PathVariable String ids);

    /***
     * 查询商品分类主键ID
     * @param levelOne 商品1级分类
     * @param levelTwo 商品2级分类
     * @param levelThree 商品3级分类
     * @param levelFour 商品4级分类
     * @param levelFive 商品5级分类
     * @return
     */
    @GetMapping("findGoodsCategoryId/{levelOne}/{levelTwo}/{levelThree}/{levelFour}/{levelFive}")
    ReturnData findGoodsCategoryId(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable int levelFour, @PathVariable int levelFive);

    /***
     * 查询商品分类(新)
     * @param levelOne 商品1级分类
     * @param levelTwo 商品2级分类
     * @param levelThree 商品3级分类
     * @return
     */
    @GetMapping("findGoodsCategorys/{levelOne}/{levelTwo}/{levelThree}")
    ReturnData findGoodsCategorys(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree);


    /***
     * 查询商品品牌
     * @param sortId 商品分类Ids
     * @param letter 商品品牌首字母
     * @return
     */
    @GetMapping("findGoodsBrand/{sortId}/{letter}")
    ReturnData findGoodsBrand(@PathVariable String sortId, @PathVariable String letter);

    /***
     * 查询商品属性名称
     * @param goodCategoryId 商品分类id
     * @param goodsBrandId 品牌id
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findBrandProperty/{goodCategoryId}/{goodsBrandId}/{page}/{count}")
    ReturnData findBrandProperty(@PathVariable long goodCategoryId, @PathVariable long goodsBrandId, @PathVariable int page, @PathVariable int count);

    /***
     * 根据多个分类、品牌查询商品属性名称
     * @param goodCategoryIds 商品分类ids
     * @param goodsBrandIds 品牌ids
     * @return
     */
    @GetMapping("findBrandPropertys/{goodCategoryIds}/{goodsBrandIds}")
    ReturnData findBrandPropertys(@PathVariable String goodCategoryIds, @PathVariable String goodsBrandIds);


    /***
     * 查询商品属性值
     * @param goodsBrandPropertyId 品牌商品属性值id
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findBrandPropertyValue/{goodsBrandPropertyId}/{page}/{count}")
    ReturnData findBrandPropertyValue(@PathVariable long goodsBrandPropertyId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询商品属性特殊名称
     * @param goodCategoryId 商品分类id
     * @param page
     * @param count
     * @return
     */
//    @GetMapping("findSpecialProperty/{goodCategoryId}/{page}/{count}")
//    ReturnData findSpecialProperty(@PathVariable long goodCategoryId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询商品特殊属性值
     * @param goodsBrandPropertyId 商品特殊属性id
     * @param page
     * @param count
     * @return
     */
//    @GetMapping("findSpecialPropertyValue/{goodsBrandPropertyId}/{page}/{count}")
//    ReturnData findSpecialPropertyValue(@PathVariable long goodsBrandPropertyId, @PathVariable int page, @PathVariable int count);

}
