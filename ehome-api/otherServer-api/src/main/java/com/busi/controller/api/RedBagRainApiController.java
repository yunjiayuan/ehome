package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 红包雨相关接口
 * author：zhaojiajie
 * create time：2018-9-12 11:03:49
 */
public interface RedBagRainApiController {

    /***
     * 查询任务完成度
     * @return
     */
    @GetMapping("findTaskList/")
    ReturnData findTaskList();

    /***
     * 拆红包
     * @param paymentKey  私钥
     * @return
     */
    @GetMapping("dismantling/{paymentKey}")
    ReturnData dismantling(@PathVariable String paymentKey);

    /***
     * 分页查询红包雨中奖列表
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findPrizeList/{userId}/{page}/{count}")
    ReturnData findPrizeList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 分页查询红包雨中奖名单列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findRedBagList/{page}/{count}")
    ReturnData findRedBagList(@PathVariable int page, @PathVariable int count);

}
