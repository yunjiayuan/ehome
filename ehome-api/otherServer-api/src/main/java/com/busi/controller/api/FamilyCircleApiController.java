package com.busi.controller.api;

import com.busi.entity.FamilyComments;
import com.busi.entity.FamilyGreeting;
import com.busi.entity.FamilyTodayPlan;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 家人圈相关接口
 * author：zhaojiajie
 * create time：2019-4-18 11:21:05
 */
public interface FamilyCircleApiController {

    /***
     * 新增家族评论
     * @param familyComments
     * @return
     */
    @PostMapping("addFComment")
    ReturnData addFComment(@Valid @RequestBody FamilyComments familyComments, BindingResult bindingResult);

    /**
     * @Description: 删除家族评论
     * @return:
     */
    @DeleteMapping("delFComment/{userId}/{ids}")
    ReturnData delFComment(@PathVariable long userId, @PathVariable String ids);

    /***
     * 分页查询家族用户评论
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findFCommentList/{userId}/{page}/{count}")
    ReturnData findFCommentList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增家族问候
     * @param familyGreeting
     * @return
     */
    @PostMapping("addGreeting")
    ReturnData addGreeting(@Valid @RequestBody FamilyGreeting familyGreeting, BindingResult bindingResult);

    /***
     * 分页查询家族问候
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findGreetingList/{userId}/{page}/{count}")
    ReturnData findGreetingList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增今日记事
     * @param familyTodayPlan
     * @return
     */
    @PostMapping("addInfor")
    ReturnData addInfor(@Valid @RequestBody FamilyTodayPlan familyTodayPlan, BindingResult bindingResult);

    /**
     * @Description: 删除今日记事
     * @return:
     */
    @DeleteMapping("delInfor/{userId}/{ids}")
    ReturnData delInfor(@PathVariable long userId, @PathVariable String ids);

    /***
     * 分页查询今日记事
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findInforList/{userId}/{page}/{count}")
    ReturnData findInforList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);
}
