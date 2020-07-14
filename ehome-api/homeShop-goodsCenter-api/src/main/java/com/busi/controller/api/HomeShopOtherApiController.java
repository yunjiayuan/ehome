package com.busi.controller.api;

import com.busi.entity.HomeShopGoodsCollection;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 二货商城商品收藏浏览相关接口
 * author：ZhaoJiaJie
 * create time：2020-07-13 14:50:31
 */
public interface HomeShopOtherApiController {
    /***
     * 新增
     * @param collect
     * @param bindingResult
     * @return
     */
    @PostMapping("addHSCollect")
    ReturnData addHSCollect(@Valid @RequestBody HomeShopGoodsCollection collect, BindingResult bindingResult);

    /**
     * @Description: 删除我的收藏
     * @return:
     */
    @DeleteMapping("delHSCollect/{ids}")
    ReturnData delHSCollect(@PathVariable String ids);

    /***
     * 分页查询我的收藏接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findHSCollect/{page}/{count}")
    ReturnData findHSCollect(@PathVariable int page, @PathVariable int count);

    /***
     * @Description: 删除我的浏览记录
     * @return:
     */
    @DeleteMapping("delHSLook/{ids}")
    ReturnData delHSLook(@PathVariable String ids);

    /***
     * 分页查询我的浏览记录接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findHSLook/{page}/{count}")
    ReturnData findHSLook(@PathVariable int page, @PathVariable int count);

    /***
     * 统计收藏、浏览次数
     * @return
     */
    @GetMapping("getHSNumber")
    ReturnData getHSNumber();
}
