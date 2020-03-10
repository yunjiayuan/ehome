package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorGoodsCollection;
import com.busi.entity.ShopFloorGoodsLook;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 楼店商品收藏浏览相关接口
 * author：ZhaoJiaJie
 * create time：2020-03-10 12:15:44
 */
public interface ShopFloorOtherApiController {
    /***
     * 新增
     * @param collect
     * @param bindingResult
     * @return
     */
    @PostMapping("addCollect")
    ReturnData addCollect(@Valid @RequestBody ShopFloorGoodsCollection collect, BindingResult bindingResult);

    /**
     * @Description: 删除我的收藏
     * @return:
     */
    @DeleteMapping("delCollect/{ids}")
    ReturnData delCollect(@PathVariable String ids);

    /***
     * 分页查询我的收藏接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findCollect/{page}/{count}")
    ReturnData findCollect(@PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除我的浏览记录
     * @return:
     */
    @DeleteMapping("delLook/{ids}")
    ReturnData delLook(@PathVariable String ids);

    /***
     * 分页查询我的浏览记录接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findLook/{page}/{count}")
    ReturnData findLook(@PathVariable int page, @PathVariable int count);
}
