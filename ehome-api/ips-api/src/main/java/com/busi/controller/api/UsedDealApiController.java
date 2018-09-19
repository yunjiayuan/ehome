package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UsedDeal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 二手相关接口
 * author：zhaojiajie
 * create time：2018-8-1 18:12:20
 */
public interface UsedDealApiController {

    /***
     * 新增二手公告
     * @param usedDeal
     * @param bindingResult
     * @return
     */
    @PostMapping("addJunk")
    ReturnData addJunk(@Valid @RequestBody UsedDeal usedDeal, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delJunk/{id}/{userId}")
    ReturnData delJunk(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新二手公告
     * @Param: usedDeal
     * @return:
     */
    @PutMapping("updateJunk")
    ReturnData updateJunk(@Valid @RequestBody UsedDeal usedDeal, BindingResult bindingResult);

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @GetMapping("getJunk/{id}")
    ReturnData getJunk(@PathVariable long id);

    /***
     * 分页查询
     * @param sort  排序条件:0默认排序，1最新发布，2价格最低，3价格最高，4离我最近
     * @param userId  用户ID
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param usedSort1  一级分类:起始值为0,默认-1为不限 :二手手机 、数码、汽车...
     * @param usedSort2  二级分类:起始值为0,默认-1为不限 : 苹果,三星,联想....
     * @param usedSort3  三级分类:起始值为0,默认-1为不限 :iPhone6s.iPhone5s....
     * @param lat  纬度
     * @param lon  经度
     * @param province  省
     * @param city  市
     * @param district  区
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findJunkList/{sort}/{userId}/{province}/{city}/{district}/{minPrice}/{maxPrice}/{usedSort1}/{usedSort2}/{usedSort3}/{lat}/{lon}/{page}/{count}")
    ReturnData findJunkList(@PathVariable int sort, @PathVariable long userId, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int minPrice, @PathVariable int maxPrice, @PathVariable int usedSort1, @PathVariable int usedSort2, @PathVariable int usedSort3, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /**
     * 根据买卖状态查询二手公告列表
     *
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @param sellType 商品买卖状态 : 1已上架，2已下架，3已卖出
     * @return
     */
    @GetMapping("findJunkState/{userId}/{sellType}/{page}/{count}")
    ReturnData findJunkState(@PathVariable long userId, @PathVariable int sellType, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 更新二手公告买卖状态
     * @Param: id  二手ID
     * @Param: sellType  商品买卖状态 : 1已上架，2已下架，3已卖出
     * @return:
     */
    @GetMapping("updateBusiness/{id}/{sellType}")
    ReturnData updateBusiness(@PathVariable long id, @PathVariable int sellType);

    /**
     * @param userId
     * @Description: 查询统计已上架, 已卖出已下架, 我的订单数量
     * @return:
     */
    @GetMapping("statisticsJunk/{userId}")
    ReturnData statisticsJunk(@PathVariable long userId);
}
