package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.LoveAndFriends;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/***
 * 婚恋交友相关接口
 * author：zhaojiajie
 * create time：2018-8-1 18:12:20
 */
public interface LoveAndFriendsApiController {

    /***
     * 新增
     * @param loveAndFriends
     * @param bindingResult
     * @return
     */
    @PostMapping("addLove")
    ReturnData addLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delLove/{id}/{userId}")
    ReturnData delLove(@PathVariable long id,@PathVariable long userId);

    /**
     * @Description: 更新
     * @Param: loveAndFriends
     * @return:
     */
    @PutMapping("updateLove")
    ReturnData updateLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult);

    /**
     * 查询
     *
     * @param id
     * @return
     */
    @GetMapping("getLove/{id}")
    ReturnData getLove(@PathVariable long id);

    /***
     * 分页条件查询
     * @param userId   用户ID
     * @param screen   性别:0不限，1男，2女
     * @param sort  0刷新时间，1年龄，2收入
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findListLove/{userId}/{screen}/{sort}/{page}/{count}")
    ReturnData findListLove(@PathVariable long userId ,@PathVariable int screen, @PathVariable int sort, @PathVariable int page, @PathVariable int count);

    /**
     * 查询是否已发布过
     * @param userId
     * @return
     */
    @GetMapping("publishedLove/{userId}")
    ReturnData publishedLove(@PathVariable long userId);

}
