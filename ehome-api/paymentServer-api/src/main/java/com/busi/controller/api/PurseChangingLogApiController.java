package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 钱包交易记录相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
public interface PurseChangingLogApiController {

    /***
     * 查询用户钱包信息
     * @param userId    将要查询的用户ID
     * @param currencyType 交易支付类型 -1所有 0钱(真实人民币),1家币,2家点
     * @param tradeType 交易类型-1所有 0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出
     * @param beginTime 查询的日期起始时间 格式为20170501
     * @param endTime   查询的日期结束时间 格式为20170530
     * @param page      页码 第几页 起始值1
     * @param count     每页条数
     * @return
     */
    @GetMapping("findPurseLogInfo/{userId}/{tradeType}/{currencyType}/{beginTime}/{endTime}/{page}/{count}")
    ReturnData findPurseLogInfo(@PathVariable long userId,@PathVariable int tradeType,@PathVariable int currencyType,
                                @PathVariable String beginTime,@PathVariable String endTime,
                                @PathVariable int page,@PathVariable int count);

}
