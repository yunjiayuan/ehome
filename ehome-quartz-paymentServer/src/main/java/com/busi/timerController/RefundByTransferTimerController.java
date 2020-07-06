package com.busi.timerController;

import com.busi.entity.TransferAccountsInfo;
import com.busi.servive.TransferAccountsInfoService;
import com.busi.utils.Constants;
import com.busi.utils.MqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 转账过期自动退款
 * @author: ZHaoJiaJie
 * @create: 2020-07-06 18:15:10
 */
@Slf4j
@Component
public class RefundByTransferTimerController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    TransferAccountsInfoService redPacketsInfoService;

    @Scheduled(cron = "0 35 1 * * ?") // 每天晚上1点35执行一次
    public void expireRedBagTimer() throws Exception {
        log.info("开始处理已过期转账...");
        TransferAccountsInfo r = null;
        int countTime = Constants.TIME_OUT_MINUTE_60_24_1 * 1000;
        long nowTime = new Date().getTime();
        List<Object> arrList = new ArrayList<Object>();
        List list = redPacketsInfoService.findEmpty();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                r = (TransferAccountsInfo) list.get(i);
                if (r != null) {
                    //判断该转账是否已过期 24小时
                    long sendTime = r.getSendTime().getTime();
                    if (nowTime - sendTime > countTime) {//已过期
                        //更新转账状态
                        r.setReceiveTime(new Date());
                        r.setTransferAccountsStatus(1);
                        redPacketsInfoService.updateTransferAccountsInfo(r);
                        //开始退款 将转账金额放回账户
                        mqUtils.sendPurseMQ(r.getSendUserId(),36,0,r.getTransferAccountsMoney());
                        arrList.add(r);
                    }
                }
            }
        }
        log.info("动态更新了(" + arrList.size() + ")个已过期转账...");
    }
}
