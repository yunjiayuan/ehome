package com.busi.controller.api;

import com.busi.entity.GraffitiChartLog;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 涂鸦记录相关接口
 * author：SunTianJie
 * create time：2018/7/30 10:45
 */
public interface GraffitiChartLogApiController {

    /***
     * 新增用户涂鸦头像接口
     * @param graffitiChartLog
     * @return
     */
    @PostMapping("addGraffitiHead")
    ReturnData addGraffitiHead(@Valid @RequestBody GraffitiChartLog graffitiChartLog, BindingResult bindingResult);

    /***
     * 删除重置涂鸦头像接口
     * @param userId
     * @return
     */
    @DeleteMapping("deleteGraffitiHead/{userId}")
    ReturnData deleteGraffitiHead(@PathVariable long userId);

    /**
     * 分页查找涂鸦记录列表接口
     * @param userId   被涂鸦者ID
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findGraffitiChartLogList/{userId}/{page}/{count}")
    ReturnData findGraffitiChartLogList(@PathVariable long userId,@PathVariable int page, @PathVariable int count);
}
