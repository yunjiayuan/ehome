package com.busi.controller.api;

import com.busi.entity.HomeBlogTag;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 生活圈兴趣标签相关接口
 * author：suntj
 * create time：2020-1-8 17:28:26
 */
public interface BlogTagApiController {


    /**
     * @Description: 新增兴趣标签
     * @Param: homeBlogTag
     * @return:
     */
    @PostMapping("addTag")
    ReturnData addTag(@Valid @RequestBody HomeBlogTag homeBlogTag, BindingResult bindingResult);

    /**
     * @Description: 更新兴趣标签
     * @Param: homeBlogTag
     * @return:
     */
    @PutMapping("updateTag")
    ReturnData updateTag(@Valid @RequestBody HomeBlogTag homeBlogTag, BindingResult bindingResult);


    /***
     * 查询兴趣标签列表
     * @return
     */
    @GetMapping("findBlogTaglist")
    ReturnData findBlogTaglist();

}
