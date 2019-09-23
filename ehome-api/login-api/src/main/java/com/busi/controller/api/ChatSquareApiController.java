package com.busi.controller.api;

import com.busi.entity.ChatAutomaticRecovery;
import com.busi.entity.ChatSquare;
import com.busi.entity.Footprint;
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
    ReturnData updateChatSquare(@Valid @RequestBody ChatSquare chatSquare, BindingResult bindingResult);

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

    /***
     * 初始化聊天广场每个聊天室在线人员
     * @param proTypeId  省Id
     * @return
     */
    @GetMapping("initialChatroom/{proTypeId}")
    ReturnData initialChatroom(@PathVariable int proTypeId);

    /***
     * 获取聊天自动回复内容
     * @param chatAutomaticRecovery
     * @param bindingResult
     * @return
     */
    @PostMapping("automaticRecoveryContent")
    ReturnData automaticRecoveryContent (@Valid @RequestBody ChatAutomaticRecovery chatAutomaticRecovery, BindingResult bindingResult);

}
