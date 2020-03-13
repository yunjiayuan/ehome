package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.PurseChangingLogControllerFegin;
import com.busi.Feign.PurseLControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.Purse;
import com.busi.entity.PurseChangingLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新钱包余额和钱包交易明细
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class PurseService implements MessageAdapter {

    @Autowired
    private PurseLControllerFegin purseLControllerFegin;

    @Autowired
    private PurseChangingLogControllerFegin purseChangingLogControllerFegin;

    /***
     * fegin 调用paymentServer服务中的 更新钱包余额操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            //将要变更的用户ID
            long userId = Long.parseLong(body.getString("userId"));
            //0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,
            // 13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出,18游戏支出，19游戏转入,20任务奖励转入,21系统奖励转入,
            // 22购买靓号支出,23小时工支出,24小时工转入,25订座点菜支出,26订座点菜转入,27楼店交易转入,28楼店交易支出,29楼店缴纳保证金支出
            // 30医生圈资费支出,31律师圈资费支出
            int tradeType = Integer.parseInt(body.getString("tradeType"));
            //交易支付类型 0钱(真实人民币),1家币,2家点
            int currencyType = Integer.parseInt(body.getString("currencyType"));
            //交易金额
            Double tradeMoney = Double.parseDouble(body.getString("tradeMoney"));
            if(userId<0||tradeType<0||tradeType>31||currencyType<0||currencyType>2){
                log.info("消息服务平台处理用户更新钱包余额功能并更新钱包明细操作失败，参数有误："+body.toJSONString());
                return;
            }
            //更新钱包余额
            Purse purse = new Purse();
            PurseChangingLog purseChangingLog = new PurseChangingLog();
            purse.setUserId(userId);
            if(currencyType==1){//家币
                purse.setHomeCoin(tradeMoney.longValue());
                purseChangingLog.setTradeMoney(tradeMoney);
            }else if(currencyType==2){//家点
                purse.setHomePoint(tradeMoney.longValue());
                purseChangingLog.setTradeMoney(tradeMoney);
            }else{//钱
                purse.setSpareMoney(tradeMoney);
                purseChangingLog.setTradeMoney(tradeMoney);
            }
            purseLControllerFegin.updatePurseInfo(purse);//fegin调用更新操作
            log.info("消息服务平台处理用户["+userId+"]更新钱包余额操作成功！");

            //更新交易记录
            purseChangingLog.setUserId(userId);
            purseChangingLog.setTradeType(tradeType);
            purseChangingLog.setCurrencyType(currencyType);
            purseChangingLogControllerFegin.addPurseChangingLog(purseChangingLog);
            log.info("消息服务平台处理用户["+userId+"]新增钱包交易明细操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户更新钱包余额功能并更新钱包明细操作失败，参数有误："+body.toJSONString());
        }
    }
}
