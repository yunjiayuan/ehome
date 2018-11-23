package com.busi.controller.local;

import com.busi.entity.HomeBlogComment;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 更新生活圈指定评论回复数相关接口
 * author：SunTianJie
 * create time：2018/10/23 9:25
 */
public interface HomeBlogCommentLocalController {

    /***
     * 更新生活圈指定评论回复数接口
     * @param homeBlogComment
     * @return
     */
    @PutMapping("updateCommentNum")
    ReturnData updateCommentNum(@RequestBody HomeBlogComment homeBlogComment);

}
