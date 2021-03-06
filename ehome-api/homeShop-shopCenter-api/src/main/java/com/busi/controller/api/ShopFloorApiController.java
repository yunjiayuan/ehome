package com.busi.controller.api;

import com.busi.entity.*;
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
     * 更新店铺配货状态
     * @param id
     * @return
     */
    @GetMapping("upDistributionStatus/{id}")
    ReturnData upDistributionStatus(@PathVariable long id);

    /***
     * 查询店铺信息
     * @param userId
     * @return
     */
    @GetMapping("findShopFloor/{userId}/{villageOnly}")
    ReturnData findShopFloor(@PathVariable long userId, @PathVariable String villageOnly);

    /***
     * 查询店铺状态
     * @param userId
     * @return
     */
    @GetMapping("findFloorState/{userId}/{villageOnly}")
    ReturnData findFloorState(@PathVariable long userId, @PathVariable String villageOnly);

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
     * 查询黑店列表（旧）
     * @param province     省 (经纬度>0时默认-1)
     * @param city      市 (经纬度>0时默认-1)
     * @param district    区 (经纬度>0时默认-1)
     * @param lat      纬度(省市区>0时默认-1)
     * @param lon      经度(省市区>0时默认-1)
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findNearbySFList/{province}/{city}/{district}/{lat}/{lon}/{page}/{count}")
    ReturnData findNearbySFList(@PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 查询黑店列表(新)
     * @param time     时间：格式yyyy-MM-dd HH:mm:ss
     * @param shopState     店铺状态  0未营业  1已营业
     * @param shopName     店铺名称 (默认null)
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findBlackSFList/{time}/{shopState}/{shopName}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findBlackSFList(@PathVariable String time, @PathVariable int shopState, @PathVariable String shopName, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count);


    /***
     * 查询黑店数量（返回格式：总数、未配货的 、已配货的）
     * @param province     省
     * @param city      市
     * @param district    区
     * @return
     */
    @GetMapping("findSFnumList/{province}/{city}/{district}")
    ReturnData findSFnumList(@PathVariable int province, @PathVariable int city, @PathVariable int district);


    /***
     * 查询用户楼店
     * @param userId   用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findUserSFlist/{userId}/{page}/{count}")
    ReturnData findUserSFlist(@PathVariable long userId, @PathVariable int page, @PathVariable int count);


    /***
     * 查询各地区黑店数量
     * @param shopState   店铺状态   -1不限 0未营业  1已营业
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findRegionSFlist/{shopState}/{page}/{count}")
    ReturnData findRegionSFlist(@PathVariable int shopState, @PathVariable int page, @PathVariable int count);

    /***
     * 按时间查询黑店数量
     * @param shopState   店铺状态   -1不限 0未营业  1已营业
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findTimeSFlist/{shopState}/{page}/{count}")
    ReturnData findTimeSFlist(@PathVariable int shopState, @PathVariable int page, @PathVariable int count);

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
     * @param levelThree 商品3级分类
     * @param letter 商品分类首字母
     * @return
     */
    @GetMapping("findYHSort/{levelOne}/{levelTwo}/{levelThree}/{letter}")
    ReturnData findYHSort(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable String letter);

    /**
     * @Description: 删除永辉分类
     * @return:
     */
    @DeleteMapping("delYHSort/{ids}")
    ReturnData delYHSort(@PathVariable String ids);

    /***
     * 新增or更新家门口坐标
     * @param shopFloorMyDoorway
     * @return
     */
    @PutMapping("editMyDoorway")
    ReturnData editMyDoorway(@Valid @RequestBody ShopFloorMyDoorway shopFloorMyDoorway, BindingResult bindingResult);

    /***
     * 查询家门口坐标
     * @return
     */
    @GetMapping("findMyDoorway")
    ReturnData findMyDoorway();
}
