package com.busi.controller.api;

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
}
