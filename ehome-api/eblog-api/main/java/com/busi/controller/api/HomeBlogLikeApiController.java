package com.busi.controller.api;

import com.busi.entity.HomeBlogLike;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 生活圈点赞相关接口
 * author：SunTianJie
 * create time：2018/10/23 9:25
 */
public interface HomeBlogLikeApiController {

    /***
     * 新增点赞接口
     * @param homeBlogLike
     * @return
     */
    @PostMapping("addHomeBlogLike")
    ReturnData addHomeBlogLike(@Valid @RequestBody HomeBlogLike homeBlogLike, BindingResult bindingResult);


    /***
     * 删除点赞接口
     * @param userId 将要删除的点赞用户ID
     * @param blogId 将要操作的生活圈ID
     * @return
     */
    @DeleteMapping("delHomeBlogLike/{userId}/{blogId}")
    ReturnData delHomeBlogLike(@PathVariable long userId, @PathVariable long blogId);

    /***
     * 条件查询点赞列表接口
     * @param blogId     将要操作的生活圈ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findHomeBlogLike/{blogId}/{page}/{count}")
    ReturnData findHomeBlogLike(@PathVariable long blogId,@PathVariable int page, @PathVariable int count);

}
