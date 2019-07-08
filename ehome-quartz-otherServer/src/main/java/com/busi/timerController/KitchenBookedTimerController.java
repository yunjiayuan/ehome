package com.busi.timerController;

import com.busi.entity.KitchenBookedOrders;
import com.busi.servive.KitchenBookedOrdersService;
import com.busi.utils.Constants;
import com.busi.utils.MqUtils;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: quartz定时器:厨房订座
 * @author: ZHaoJiaJie
 * @create: 2019-07-03 15:52
 */
@Slf4j
@Component
public class KitchenBookedTimerController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    KitchenBookedOrdersService kitchenBookedOrdersService;

    @Scheduled(cron = "0 40 10 * * ?") //十五点五十五分
    public void kitchenTimer() throws Exception {
        log.info("开始查询数据库中待处理的厨房订座超时订单...");
        while (true) {
            List arrList = null;
            KitchenBookedOrders r = null;
            int countTime15 = 15 * 60 * 1000;// 15分钟
            long nowTime = new Date().getTime();// 系统时间
            arrList = kitchenBookedOrdersService.findOrderList();
            if (arrList != null && arrList.size() > 0) {
                for (int i = 0; i < arrList.size(); i++) {
                    r = (KitchenBookedOrders) arrList.get(i);
                    if (r != null) {
                        long sendTime = r.getAddTime().getTime();// 下单时间

                        if (r.getOrdersType() == 0) {
                            if (sendTime <= nowTime - countTime15) {
                                r.setOrdersType(5);// 付款超时【未付款】
                                kitchenBookedOrdersService.updateOrders(r);
                                //清除缓存中的厨房订单信息
                                redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                                log.info("更新了厨房订座订单[" + r.getId() + "]操作成功,状态为：付款超时！");
                                arrList.remove(i);
                            } else {
                                continue;
                            }
                        } else if (r.getOrdersType() == 1) {
                            long paymentTime = r.getPaymentTime().getTime();// 付款时间
                            if (paymentTime <= nowTime - (countTime15 * 2)) {//30分钟时间差
                                r.setOrdersType(4);// 接单超时【未接单】
                                kitchenBookedOrdersService.updateOrders(r);
                                //更新买家缓存、钱包、账单
                                mqUtils.sendPurseMQ(r.getMyId(), 16, 0, r.getMoney());
                                //清除缓存中的厨房订单信息
                                redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                                log.info("更新了厨房订座订单[" + r.getId() + "]操作成功,状态为：接单超时！");
                                arrList.remove(i);
                            } else {
                                continue;
                            }
                        } else {
                            arrList.remove(i);
                            continue;
                        }
                    }
                }
            }
        }
    }
}
