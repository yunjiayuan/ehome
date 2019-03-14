package com.busi.controller.api;

import com.busi.entity.RedPacketsInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 *  聊天系统 红包相关接口
 * author：SunTianJie
 * create time：2018/9/7 10:04
 */
public interface RedPacketsInfoApiController {

    /***
     * 发送红包接口
     * @param redPacketsInfo
     * @return
     */
    @PostMapping("addRedPacketsInfo")
    ReturnData addRedPacketsInfo(@Valid @RequestBody RedPacketsInfo redPacketsInfo, BindingResult bindingResult);

    /***
     * 根据红包ID查询红包信息
     * @param id
     * @return
     */
    @GetMapping("findRedPacketsInfo/{id}")
    ReturnData findRedPacketsInfo(@PathVariable String id);

    /***
     * 接收(拆)红包后留言接口
     * @param redPacketsInfo
     * @return
     */
    @PutMapping("receiveMessage")
    ReturnData receiveMessage(@RequestBody RedPacketsInfo redPacketsInfo);

    /***
     * 查询红包记录列表
     * @param userId   被查询用户ID
     * @param findType 1查询我发的红包 2查询我收的红包列表
     * @param time     查询年份 格式2017  起始值2017
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findRedPacketsList/{userId}/{findType}/{time}/{page}/{count}")
    ReturnData findRedPacketsList(@PathVariable long userId,@PathVariable int findType,@PathVariable int time,@PathVariable int page,@PathVariable int count);

    /***
     * 根据用户ID查询红包统计信息接口
     * @param userId
     * @return
     */
    @GetMapping("findRedPacketsCensusInfo/{userId}")
    ReturnData findRedPacketsCensusInfo(@PathVariable long userId);
}
