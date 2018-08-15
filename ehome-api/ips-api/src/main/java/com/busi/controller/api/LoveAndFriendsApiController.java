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
     * @param sex   性别:0不限，1男，2女
     * @param income 收入:0不限，1（<3000），2（3000-5000），3（5000-7000），4（7000-9000），5（9000-12000），6（12000-15000），7（15000-20000），8（>20000）
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findListLove/{sex}/{income}/{page}/{count}")
    ReturnData findListLove(@PathVariable int sex, @PathVariable int income, @PathVariable int page, @PathVariable int count);

}
