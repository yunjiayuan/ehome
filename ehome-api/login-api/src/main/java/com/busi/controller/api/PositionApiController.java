package com.busi.controller.api;

import com.busi.entity.PositionInfo;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
