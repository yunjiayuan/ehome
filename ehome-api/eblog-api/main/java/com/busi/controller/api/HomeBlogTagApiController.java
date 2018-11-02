package com.busi.controller.api;

import com.busi.entity.HomeBlogUserTag;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 生活圈兴趣标签相关接口
 * author：zhaojiajie
 * create time：2018-11-1 15:34:33
 */
public interface HomeBlogTagApiController {

    /**
     * @Description: 更新兴趣标签
     * @Param: homeBlogUserTag
     * @return:
     */
    @PutMapping("editUserTag")
    ReturnData editUserTag(@Valid @RequestBody HomeBlogUserTag homeBlogUserTag, BindingResult bindingResult);


    /***
     * 查询兴趣标签
     * @param userId     查询用户ID
     * @return
     */
    @GetMapping("findTaglist/{userId}")
    ReturnData findTaglist(@PathVariable long userId);

}
