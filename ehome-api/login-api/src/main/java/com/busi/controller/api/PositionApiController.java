package com.busi.controller.api;

import com.busi.entity.PositionInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 位置信息接口 提供更新位置信息 和 删除位置信息
 * author：SunTianJie
 * create time：2018/7/12 16:36
 */
public interface PositionApiController {

    /***
     * 更新位置信息
     * @param positionInfo
     * @return
     */
    @PutMapping("updatePosition")
    ReturnData updatePosition(@Valid @RequestBody PositionInfo positionInfo, BindingResult bindingResult);

    /***
     * 删除位置信息
     * @return
     */
    @DeleteMapping("delPosition")
    ReturnData delPosition();

    /***
     * 获取指定用户ID的位置信息
     * @param userId 被查询者的用户ID
     * @return
     */
    @GetMapping("findPositionInfo/{userId}")
    ReturnData findPositionInfo(@PathVariable long userId);
}
