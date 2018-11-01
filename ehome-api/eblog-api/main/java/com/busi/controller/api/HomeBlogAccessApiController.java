package com.busi.controller.api;

import com.busi.entity.HomeBlogAccess;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 生活圈标签相关接口
 * author：zhaojiajie
 * create time：2018-11-1 15:34:33
 */
public interface HomeBlogAccessApiController {

    /***
     * 添加标签接口
     * @param homeBlogAccess
     * @return
     */
    @PostMapping("addLabel")
    ReturnData addLabel(@Valid @RequestBody HomeBlogAccess homeBlogAccess, BindingResult bindingResult);

    /**
     * @Description: 更新标签
     * @Param: homeBlogAccess
     * @return:
     */
    @PutMapping("updateLabel")
    ReturnData updateLabel(@Valid @RequestBody HomeBlogAccess homeBlogAccess, BindingResult bindingResult);

    /***
     * 删除指定标签接口
     * @param userId 用户ID
     * @param tagId 将要被删除的标签
     * @return
     */
    @DeleteMapping("delLabel/{userId}/{tagId}")
    ReturnData delLabel(@PathVariable long userId, @PathVariable long tagId);

    /***
     * 查询指定标签内成员接口
     * @param tagId     被查询标签ID
     * @return
     */
    @GetMapping("findMemberList/{tagId}")
    ReturnData findMemberList(@PathVariable long tagId);

    /***
     * 查询标签列表
     * @param userId     用户ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findLabelList/{userId}/{page}/{count}")
    ReturnData findLabelList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

}
