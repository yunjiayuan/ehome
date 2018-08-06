package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserRelationShip;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 好友关系相关接口
 * author：SunTianJie
 * create time：2018/7/16 17:20
 */
public interface UserRelationShipApiController {

    /***
     * 新增好友关系接口
     * @param userRelationShip
     * @return
     */
    @PostMapping("addFriend")
    ReturnData addFriend(@Valid @RequestBody UserRelationShip userRelationShip, BindingResult bindingResult);

    /***
     * 删除好友接口
     * @param friendId 将要删除的好友ID
     * @return
     */
    @DeleteMapping("delFriend/{friendId}")
    ReturnData delFriend(@PathVariable long friendId);

    /***
     * 获取好友列表接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findFriendList/{page}/{count}")
    ReturnData findFriendList(@PathVariable int page, @PathVariable int count);

    /***
     * 修改备注接口
     * @param userRelationShip
     * @return
     */
    @PutMapping("updateRemarkName")
    ReturnData updateRemarkName (@Valid @RequestBody UserRelationShip userRelationShip, BindingResult bindingResult);

    /***
     *  将好友移动分组接口
     * @param userRelationShip
     * @return
     */
    @PutMapping("moveFriend")
    ReturnData moveFriend (@Valid @RequestBody UserRelationShip userRelationShip, BindingResult bindingResult);

    /***
     * 批量查询手机号是否绑定云家园账号接口
     * @param phones
     * @return
     */
    @GetMapping("searchPhoneForContacts/{phones}")
    ReturnData searchPhoneForContacts (@PathVariable String phones);
}
