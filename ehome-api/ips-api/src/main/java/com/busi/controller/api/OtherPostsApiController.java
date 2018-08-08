package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.busi.entity.OtherPosts;
import javax.validation.Valid;

/***
 * 其他公告相关接口
 * author：zhaojiajie
 * create time：2018-8-7 15:12:17
 */
public interface OtherPostsApiController {

    /***
     * 新增
     * @param otherPosts
     * @param bindingResult
     * @return
     */
    @PostMapping("addOther")
    ReturnData addOther(@Valid @RequestBody OtherPosts otherPosts, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delOther/{id}/{userId}")
    ReturnData delOther(@PathVariable long id,@PathVariable long userId);

    /**
     * @Description: 更新
     * @Param: otherPosts
     * @return:
     */
    @PutMapping("updateOther")
    ReturnData updateOther(@Valid @RequestBody OtherPosts otherPosts, BindingResult bindingResult);

    /**
     * 查询
     *
     * @param id
     * @return
     */
    @GetMapping("getOther/{id}")
    ReturnData getOther(@PathVariable long id);

    /***
     * 分页查询
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findOtherList/{userId}/{page}/{count}")
    ReturnData findOtherList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

}
