package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 喂鸟相关接口
 * author：zhaojiajie
 * create time：2018-9-4 14:09:45
 */
interface BirdJournalApiController {

    /**
     * 新增喂鸟记录
     *
     * @param userId
     * @return
     */
    @GetMapping("addBirdLog/{userId}")
    ReturnData addBirdLog(@PathVariable long userId);

    /**
     * @Description: 删除喂鸟记录
     * @return:
     */
    @DeleteMapping("delBirdRecord/{id}/{userId}")
    ReturnData delBirdRecord(@PathVariable long id, @PathVariable long userId);

    /**
     * 查询剩余次数与今日是否喂过此鸟
     *
     * @param userId
     * @return
     */
    @GetMapping("getRemainder/{userId}")
    ReturnData getRemainder(@PathVariable long userId);

    /**
     * 查询鸟蛋状态
     *
     * @param userId
     * @return
     */
    @GetMapping("findBirdEgg/{userId}")
    ReturnData findBirdEgg(@PathVariable long userId);

    /**
     * 砸鹦鹉蛋
     *
     * @param userId
     * @return
     */
    @GetMapping("hitEgg/{userId}/{hitEggType}/{issue}")
    ReturnData hitEgg(@PathVariable long userId, @PathVariable int hitEggType, @PathVariable int issue);

    /**
     * 查询最新一期奖品
     *
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @return
     */
    @GetMapping("findNewPrize/{eggType}")
    ReturnData findNewPrize(@PathVariable int eggType);

    /***
     * 分页查询喂鸟记录
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param state 0喂我的  1我喂的
     * @param count 每页条数
     * @return
     */
    @GetMapping("findBirdList/{userId}/{state}/{page}/{count}")
    ReturnData findBirdList(@PathVariable long userId, @PathVariable int state, @PathVariable int page, @PathVariable int count);

    /***
     * 分页查询砸蛋记录
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findSmashList/{userId}/{page}/{count}")
    ReturnData findSmashList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 分页查询自己奖品
     * @param userId  用户ID
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findOwnList/{userId}/{eggType}/{page}/{count}")
    ReturnData findOwnList(@PathVariable long userId, @PathVariable int eggType, @PathVariable int page, @PathVariable int count);

    /***
     * 分页查询中奖名单
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param issue 期号
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findWinningList/{eggType}/{issue}/{page}/{count}")
    ReturnData findWinningList(@PathVariable int eggType, @PathVariable int issue, @PathVariable int page, @PathVariable int count);

}
