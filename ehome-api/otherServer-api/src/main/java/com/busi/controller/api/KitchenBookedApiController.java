package com.busi.controller.api;

import com.busi.entity.KitchenBooked;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 厨房订座相关接口
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 16:40
 */
public interface KitchenBookedApiController {

    /***
     * 新增订座信息
     * @param kitchenBooked
     * @return
     */
    @PostMapping("addKitchenBooked")
    ReturnData addKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult);

    /***
     * 查看订座设置详情
     * @param userId  商家ID
     * @return
     */
    @GetMapping("findKitchenBooked/{userId}")
    ReturnData findKitchenBooked(@PathVariable long userId);

    /***
     * 编辑订座设置
     * @param kitchenBooked
     * @return
     */
    @PutMapping("changeKitchenBooked")
    ReturnData changeKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult);
}
