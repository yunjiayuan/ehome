package com.busi.controller.api;

import com.busi.entity.PassProve;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 社区出入证、证明相关接口
 * author：ZJJ
 * create time：2021-02-03 14:41:16
 */
public interface PassProveApiController {

    /***
     * 新增出入证、证明
     * @param communityEventReporting
     * @return
     */
    @PostMapping("addPassProve")
    ReturnData addPassProve(@Valid @RequestBody PassProve communityEventReporting, BindingResult bindingResult);

    /***
     * 审核出入证、证明
     * @param communityEventReporting
     * @return
     */
    @PutMapping("toExaminePassProve")
    ReturnData toExaminePassProve(@Valid @RequestBody PassProve communityEventReporting, BindingResult bindingResult);

    /***
     * 查询用户状态
     * @param communityId    居委会ID
     * @param type    0出入证  1证明
     * @return
     */
    @GetMapping("findUserPassProve/{communityId}/{userId}/{type}")
    ReturnData findUserPassProve(@PathVariable long communityId, @PathVariable long userId, @PathVariable int type);

    /***
     * 根据ID查询出入证、证明详情
     * @param id
     * @return
     */
    @GetMapping("findPassProve/{id}")
    ReturnData findPassProve(@PathVariable long id);

    /***
     * 查询出入证、证明列表
     * @param communityId    居委会ID
     * @param type     -1表示查询所有 0出入证 1证明
     * @param auditType     -1表示查询所有 0待审核 1已审核通过 2未审核通过
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPassProveList/{communityId}/{userId}/{type}/{auditType}/{page}/{count}")
    ReturnData findPassProveList(@PathVariable long communityId, @PathVariable long userId, @PathVariable int type, @PathVariable int auditType, @PathVariable int page, @PathVariable int count);

    /***
     * 统计各种审核状态数量
     * @param communityId    居委会ID
     * @param type    0出入证  1证明
     * @return
     */
    @GetMapping("countPassProveAuditType/{communityId}/{type}")
    ReturnData countPassProveAuditType(@PathVariable long communityId, @PathVariable int type);
}
