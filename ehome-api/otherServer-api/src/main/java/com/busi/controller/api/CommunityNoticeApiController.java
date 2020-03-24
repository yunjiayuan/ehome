package com.busi.controller.api;

import com.busi.entity.CommunityNotice;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 公告相关接口
 * author：zhaojiajie
 * create time：2020-03-23 16:22:04
 */
public interface CommunityNoticeApiController {

    /***
     * 新增
     * @param todayNotice
     * @param bindingResult
     * @return
     */
    @PostMapping("addNotice")
    ReturnData addNotice(@Valid @RequestBody CommunityNotice todayNotice, BindingResult bindingResult);

    /**
     * @Description: 更新
     * @Param: todayNotice
     * @return:
     */
    @PutMapping("editNotice")
    ReturnData editNotice(@Valid @RequestBody CommunityNotice todayNotice, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delNotice/{ids}")
    ReturnData delNotice(@PathVariable String ids);

    /***
     * 查询列表
     * @param communityId 居委会ID
     * @param type 类型 0居委会  1物业
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findNoticeList/{communityId}/{type}/{page}/{count}")
    ReturnData findNoticeList(@PathVariable long communityId,@PathVariable int type, @PathVariable int page, @PathVariable int count);
}
