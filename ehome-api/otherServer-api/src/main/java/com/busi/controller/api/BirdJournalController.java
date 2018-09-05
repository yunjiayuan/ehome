package com.busi.controller.api;

import com.busi.entity.BirdFeedingData;
import com.busi.entity.BirdFeedingRecord;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 喂鸟相关接口
 * author：zhaojiajie
 * create time：2018-9-4 14:09:45
 */
interface BirdJournalApiController {

   /***
     * 新增喂鸟详细记录
     * @param visitId
     * @return
     */
   @GetMapping("addBirdLog/{visitId}")
    ReturnData addBirdLog(@PathVariable long visitId);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delBird/{id}/{userId}")
    ReturnData delBird(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新
     * @Param: birdFeedingData
     * @return:
     */
    @PutMapping("updateBird")
    ReturnData updateBird(@Valid @RequestBody BirdFeedingData birdFeedingData, BindingResult bindingResult);

    /**
     * 查询
     * @param id
     * @return
     */
    @GetMapping("getBird/{id}")
    ReturnData getBird(@PathVariable long id);

    /***
     * 分页查询
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findBirdList/{userId}/{page}/{count}")
    ReturnData findBirdList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);
}
