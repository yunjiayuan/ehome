package com.busi.controller.api;

import com.busi.entity.ChatSquare;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 聊天广场马甲功能接口
 * author：SunTianJie
 * create time：2018/10/10 16:19
 */
public interface ChatSquareApiController {

    /***
     * 查询用户马甲信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @GetMapping("findChatSquare/{userId}")
    ReturnData findChatSquare(@PathVariable long userId);

    /***
     * 新增或修改用户马甲接口
     * @param chatSquare
     * @return
     */
    @PutMapping("updateChatSquare")
    ReturnData updateChatSquare (@Valid @RequestBody ChatSquare chatSquare, BindingResult bindingResult);

    /***
     * 清除用户马甲信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @DeleteMapping("delChatSquare/{userId}")
    ReturnData delChatSquare(@PathVariable long userId);

    /***
     * 查询指定聊天室成员列表的用户信息
     * @param userIds 将要查询的用户ID组合 格式123,456
     * @return
     */
    @GetMapping("findChatSquareUserInfo/{userIds}")
    ReturnData findChatSquareUserInfo(@PathVariable String userIds);
}
