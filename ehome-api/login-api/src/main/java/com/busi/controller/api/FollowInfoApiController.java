package com.busi.controller.api;

import com.busi.entity.FollowInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 关注相关接口（全局关注接口 不仅仅适用于生活圈）
 * author：SunTianJie
 * create time：2018/10/23 9:25
 */
public interface FollowInfoApiController {

    /***
     * 加关注
     * @param followInfo
     * @return
     */
    @PostMapping("addFollow")
    ReturnData addFollow(@Valid @RequestBody FollowInfo followInfo, BindingResult bindingResult);

    /***
     * 取消关注
     * @param userId       生活圈发布者用户ID
     * @param followUserId 将要被删除的生活圈
     * @return
     */
    @DeleteMapping("delFollow/{userId}/{followUserId}")
    ReturnData delFollow(@PathVariable long userId, @PathVariable long followUserId);

    /***
     * 查询关注和粉丝列表
     * @param userId     将要查询的用户ID
     * @param searchType 0 表示查询我关注的人列表  1表示关注我的用户列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findFollowList/{userId}/{searchType}/{page}/{count}")
    ReturnData findFollowList(@PathVariable long userId, @PathVariable int searchType,
                            @PathVariable int page, @PathVariable int count);

}
