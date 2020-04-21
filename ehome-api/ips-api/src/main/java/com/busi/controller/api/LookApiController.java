package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * look
 * author：zhaojiajie
 * create time：2018-8-24 15:26:44
 */
public interface LookApiController {

    /**
     * @Description: 删除我的浏览记录
     * @return:
     */
    @DeleteMapping("delLook/{myId}/{ids}")
    ReturnData delLook(@PathVariable long myId, @PathVariable String ids);

    /***
     * 分页查询我的浏览记录接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findLook/{userId}/{afficheType}/{page}/{count}")
    ReturnData findLook(@PathVariable long userId, @PathVariable int afficheType, @PathVariable int page, @PathVariable int count);

}
