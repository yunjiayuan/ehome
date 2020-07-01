package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 赢大奖相关接口
 * author：zhaojiajie
 * create time：2018-9-14 12:06:13
 */
public interface PrizesLuckyDrawApiController {

    /***
     * 参加活动
     * @param issue 期数
     * @return
     */
    @GetMapping("participateIn/{issue}")
    ReturnData participateIn(@PathVariable int issue);

    /***
     * 领奖
     * @param infoId  领奖ID
     * @return
     */
    @GetMapping("takeThePrize/{infoId}/{address}/{contactsName}/{contactsPhone}/{postalcode}")
    ReturnData takeThePrize(@PathVariable long infoId, @PathVariable String address, @PathVariable String contactsName, @PathVariable String contactsPhone, @PathVariable String postalcode);

    /***
     * 查询中奖人员列表
     * @param issue  期数
     * @param grade  奖品等级：1一等奖 2纪念奖
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findWinningList/{issue}/{grade}/{page}/{count}")
    ReturnData findWinningList(@PathVariable int issue, @PathVariable int grade, @PathVariable int page, @PathVariable int count);

    /***
     * 查询自己奖品
     * @param openTime 开奖时间
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findOwnList/{openTime}/{page}/{count}")
    ReturnData findOwnList(@PathVariable String openTime, @PathVariable int page, @PathVariable int count);

    /***
     * 查询最新一期奖品
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findNewList/{page}/{count}")
    ReturnData findNewList(@PathVariable int page, @PathVariable int count);

    /***
     * 查询最新一期纪念奖奖品
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findMemorialList/{page}/{count}")
    ReturnData findMemorialList(@PathVariable int page, @PathVariable int count);

}
