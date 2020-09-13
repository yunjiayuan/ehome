package com.busi.timerController;

import com.busi.Feign.CashOutLControllerFegin;
import com.busi.entity.CashOutOrder;
import com.busi.servive.CashOutService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: ehome
 * @description: 处理未到账提现
 * @author: ZhaoJiaJie
 * @create: 2020-07-10 14:25:33
 */
@Slf4j
@Component
public class CashOutTimerController {

    @Autowired
    private CashOutService cashOutService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CashOutLControllerFegin cashOutLControllerFegin;

    @Scheduled(cron = "0 33 1 * * ?") // 每天晚上1点33执行一次
    public void expireRedBagTimer() throws Exception {
        log.info("开始处理未到账提现...");
        CashOutOrder r = null;
        List<Object> arrList = new ArrayList<Object>();
        List list = cashOutService.findCashOutOrderList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                r = (CashOutOrder) list.get(i);
                if (r != null) {
                    //调用paymentServer服务中的 更新钱包余额操作
                    //将订单放入缓存中  15分钟有效时间  超时作废
                    redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_CASHOUT+r.getId(),CommonUtils.objectToMap(r),Constants.TIME_OUT_MINUTE_15);
                    cashOutLControllerFegin.cashOutToOther(r);
                    log.info("消息服务平台处理用户[" + r.getUserId() + "]提现操作成功！");
                    arrList.add(r);
                }
            }
        }
        log.info("动态更新了(" + arrList.size() + ")个提现操作...");
    }
}
