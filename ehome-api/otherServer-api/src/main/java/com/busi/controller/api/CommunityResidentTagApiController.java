package com.busi.controller.api;

import com.busi.entity.CommunityResidentTag;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 居委会居民标签相关
 * @author: ZHaoJiaJie
 * @create: 2020-03-17 17:07:43
 */
public interface CommunityResidentTagApiController {
    /**
     * @Description: 新增标签
     * @Param: residentTag
     * @return:
     */
    @PostMapping("addTag")
    ReturnData addTag(@Valid @RequestBody CommunityResidentTag residentTag, BindingResult bindingResult);

    /**
     * @Description: 更新标签
     * @Param: residentTag
     * @return:
     */
    @PutMapping("updateTag")
    ReturnData updateTag(@Valid @RequestBody CommunityResidentTag residentTag, BindingResult bindingResult);


    /***
     * 查询标签列表
     * @return
     */
    @GetMapping("findTaglist/{id}")
    ReturnData findTaglist(@PathVariable long id);

    /**
     * @Description: 删除标签
     * @return:
     */
    @DeleteMapping("delTags/{ids}")
    ReturnData delTags(@PathVariable String ids);
}
