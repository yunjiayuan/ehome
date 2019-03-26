package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.SelfChannel;
import com.busi.entity.SelfChannelVipOrder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 自频道相关接口
 * author：zhaojiajie
 * create time：2019-3-20 15:53:38
 */
public interface SelfChannelVipApiController {

    /***
     * 新增订单
     * @param selfChannelVipOrder
     * @param bindingResult
     * @return
     */
    @PostMapping("addGearShiftOrder")
    ReturnData addGearShiftOrder(@Valid @RequestBody SelfChannelVipOrder selfChannelVipOrder, BindingResult bindingResult);

    /***
     * 查询用户是否是自频道会员
     * @param userId
     * @return
     */
    @GetMapping("rightVip/{userId}")
    ReturnData rightVip(@PathVariable long userId);

    /***
     * 新增排挡
     * @param selfChannel
     * @param bindingResult
     * @return
     */
    @PostMapping("addGearShift")
    ReturnData addGearShift(@Valid @RequestBody SelfChannel selfChannel, BindingResult bindingResult);

    /***
     * 查询排挡视频列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findGearShiftList/{page}/{count}")
    ReturnData findGearShiftList(@PathVariable int page, @PathVariable int count);

}
