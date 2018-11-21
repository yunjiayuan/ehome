package com.busi.controller.api;

import com.busi.entity.HomeBlogComment;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 生活圈评论相关接口
 * author：ZHaoJiaJie
 * create time：2018-11-5 14:00:38
 */
public interface HomeBlogCommentApiController {

    /***
     * 生活圈添加评论接口
     * @param homeBlogComment
     * @return
     */
    @PostMapping("addComment")
    ReturnData addComment(@Valid @RequestBody HomeBlogComment homeBlogComment, BindingResult bindingResult);

    /***
     * 删除生活圈评论接口
     * @param id 评论ID
     * @param blogId 生活圈ID
     * @return
     */
    @DeleteMapping("delComment/{id}/{blogId}")
    ReturnData delComment(@PathVariable long id, @PathVariable long blogId);

    /***
     * 查询生活圈评论记录接口
     * @param blogId     生活圈ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findCommentList/{type}/{blogId}/{page}/{count}")
    ReturnData findCommentList(@PathVariable int type,@PathVariable long blogId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询生活圈指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findReplyList/{contentId}/{page}/{count}")
    ReturnData findReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count);

}
