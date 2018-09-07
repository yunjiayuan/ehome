package com.busi.controller.api;

import com.busi.entity.RedPacketsInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    ReturnData addRedPacketsInfo(@Valid  @RequestBody RedPacketsInfo redPacketsInfo, BindingResult bindingResult);

    /***
     * 根据红包ID查询红包信息
     * @param id
     * @return
     */
    @GetMapping("findRedPacketsInfo/{id}")
    ReturnData findRedPacketsInfo(@PathVariable long id);

}
