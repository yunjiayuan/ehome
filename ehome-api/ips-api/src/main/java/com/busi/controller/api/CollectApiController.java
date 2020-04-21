package com.busi.controller.api;

import com.busi.entity.Collect;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * look
 * author：zhaojiajie
 * create time：2018-8-24 15:26:44
 */
public interface CollectApiController {

    /***
     * 新增
     * @param collect
     * @param bindingResult
     * @return
     */
    @PostMapping("addCollect")
    ReturnData addCollect(@Valid @RequestBody Collect collect, BindingResult bindingResult);

    /**
     * @Description: 删除我的收藏
     * @return:
     */
    @DeleteMapping("delCollect/{myId}/{ids}")
    ReturnData delCollect(@PathVariable long myId, @PathVariable String ids);

    /**
     * 统计收藏次数
     *
     * @param infoId
     * @param afficheType
     * @return
     */
    @GetMapping("getCollect/{infoId}/{afficheType}")
    ReturnData getCollect(@PathVariable long infoId, @PathVariable int afficheType);

    /***
     * 分页查询我的收藏接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findCollect/{userId}/{afficheType}/{page}/{count}")
    ReturnData findCollect(@PathVariable long userId, @PathVariable int afficheType, @PathVariable int page, @PathVariable int count);
}
