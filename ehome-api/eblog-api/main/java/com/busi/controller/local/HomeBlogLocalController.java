package com.busi.controller.local;

import com.busi.entity.HomeBlog;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.*;

/**
 * 更新生活圈评论数、点赞数、浏览量、转发量相关接口
 * author：SunTianJie
 * create time：2018/10/23 9:25
 */
public interface HomeBlogLocalController {

    /***
     * 更新生活圈发布接口
     * @param homeBlog
     * @return
     */
    @PutMapping("updateBlog")
    ReturnData updateBlog(@RequestBody HomeBlog homeBlog);


}
