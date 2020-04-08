package com.busi.controller.api;

import com.busi.entity.CommunityRepair;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 社区报修相关接口
 * author：ZJJ
 * create time：2020-04-08 16:53:12
 */
public interface CommunityRepairApiController {

    /***
     * 新增报修
     * @param homeHospital
     * @return
     */
    @PostMapping("addRepair")
    ReturnData addRepair(@Valid @RequestBody CommunityRepair homeHospital, BindingResult bindingResult);

    /***
     * 删除报修
     * @return:
     */
    @DeleteMapping("delRepair/{ids}")
    ReturnData delRepair(@PathVariable String ids);

    /***
     * 查询报修列表
     * @param type    type=0居委会  type=1物业
     * @param communityId   type=0时居委会ID  type=1时物业ID
     * @param userId   查询者
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findRepairList/{type}/{communityId}/{userId}/{page}/{count}")
    ReturnData findRepairList(@PathVariable int type, @PathVariable long communityId, @PathVariable long userId, @PathVariable int page, @PathVariable int count);


}
