package com.busi.controller.api;

import com.busi.entity.GrabMedium;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 抢礼物相关接口
 * author：zhaojiajie
 * create time：2020-04-03 10:22:21
 */
public interface GrabGiftsApiController {

    /***
     * 抢礼物
     * @param grabMedium
     * @return
     */
    @PostMapping("grabMedium")
    ReturnData grabMedium(@Valid @RequestBody GrabMedium grabMedium, BindingResult bindingResult);

    /***
     * 查询中奖人员列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findWinList/{page}/{count}")
    ReturnData findWinList(@PathVariable int page, @PathVariable int count);

    /***
     * 查询自己的纪录
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findMyList/{page}/{count}")
    ReturnData findMyList(@PathVariable int page, @PathVariable int count);

    /***
     * 查询奖品
     * @return
     */
    @GetMapping("findGifts")
    ReturnData findGifts();
}
