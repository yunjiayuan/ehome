package com.busi.controller.api;

import com.busi.entity.BigVAccountsExamine;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 大V审核相关接口
 * author：zhaojiajie
 * create time：2020-07-07 15:18:53
 */
public interface BigVAccountsExamineApiController {

    /***
     * 新增审核
     * @param homeAlbum
     * @return
     */
    @PostMapping("addBigVExamine")
    ReturnData addBigVExamine(@Valid @RequestBody BigVAccountsExamine homeAlbum, BindingResult bindingResult);

    /***
     * 查询审核列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findBigVExamineList/{page}/{count}")
    ReturnData findBigVExamineList(@PathVariable int page, @PathVariable int count);

    /***
     * 更新审核状态
     * @param homeAlbum
     * @return
     */
    @PutMapping("changeBigVExamineState")
    ReturnData changeBigVExamineState(@Valid @RequestBody BigVAccountsExamine homeAlbum, BindingResult bindingResult);

}
