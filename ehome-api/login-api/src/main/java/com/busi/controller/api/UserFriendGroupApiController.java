package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserFriendGroup;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 好友分组相关接口
 * author：SunTianJie
 * create time：2018/7/16 17:20
 */
public interface UserFriendGroupApiController {

    /***
     * 新增好友分组接口
     * @param userFriendGroup
     * @return
     */
    @PostMapping("addFriendGroup")
    ReturnData addFriendGroup(@Valid @RequestBody UserFriendGroup userFriendGroup, BindingResult bindingResult);

    /***
     * 删除好友分组
     * @param groupId 将要删除的分组ID
     * @return
     */
    @DeleteMapping("delFriendGroup/{groupId}")
    ReturnData delFriendGroup(@PathVariable long groupId);

    /***
     * 获取好友分组列表接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findFriendGroupList/{page}/{count}")
    ReturnData findFriendGroupList(@PathVariable int page, @PathVariable int count);

    /***
     * 修改分组名接口
     * @param userFriendGroup
     * @return
     */
    @PutMapping("updateGroupName")
    ReturnData updateGroupName(@Valid @RequestBody UserFriendGroup userFriendGroup, BindingResult bindingResult);

}
