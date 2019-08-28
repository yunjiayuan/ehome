package com.busi.timerController;

import com.busi.entity.KitchenOrders;
import com.busi.servive.KitchenOrdersService;
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
 * quartz定时器:厨房
 * author：ZHaoJiaJie
 * create time：2019-3-11 13:29:28
 */
@Slf4j
@Component
public class KitchenTimerController {
    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    KitchenOrdersService kitchenOrdersService;

    /**
     * Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
     * <p>
     * “*”可用在所有字段中，表示对应时间域的每一个时刻，例如，*在分钟字段时，表示“每分钟”；
     * <p>
     * “?”字符：表示不确定的值 该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于点位符；
     * <p>
     * “,”字符：指定数个值 表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五；
     * <p>
     * “-”字符：指定一个值的范围 如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12；
     * <p>
     * “/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m
     * <p>
     * “L”字符：用在日表示一个月中的最后一天，用在周表示该月最后一个星期X
     * <p>
     * “W”字符：指定离给定日期最近的工作日(周一到周五)
     * <p>
     * “#”字符：表示该月第几个周X。6#3表示该月第3个周五
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 30 16 * * ?") //十五点五十五分
    public void kitchenTimer() throws Exception {
        log.info("开始查询数据库中待处理的厨房超时订单...");
        while (true) {
            List arrList = null;
            KitchenOrders r = null;
            int countTime15 = 15 * 60 * 1000;// 15分钟
            int countTime6 = 6 * 60 * 60 * 1000;// 6小时
            int countTime24 = 24 * 60 * 60 * 1000;// 24小时
            long nowTime = new Date().getTime();// 系统时间
            arrList = kitchenOrdersService.findOrderList();
            if (arrList != null && arrList.size() > 0) {
                for (int i = 0; i < arrList.size(); i++) {
                    r = (KitchenOrders) arrList.get(i);
                    if (r != null) {
                        long sendTime = r.getAddTime().getTime();// 下单时间

                        if (r.getOrdersType() == 0) {
                            if (sendTime <= nowTime - countTime15) {
                                r.setOrdersType(6);// 付款超时【未付款】
                                kitchenOrdersService.updateOrders(r);
                                //清除缓存中的厨房订单信息
                                redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                                log.info("更新了厨房订单[" + r.getId() + "]操作成功,状态为：付款超时！");
                                arrList.remove(i);
                            } else {
                                continue;
                            }
                        } else if (r.getOrdersType() == 1) {
                            long paymentTime = r.getPaymentTime().getTime();// 付款时间
                            if (paymentTime <= nowTime - (countTime15 * 2)) {//30分钟时间差
                                r.setOrdersType(7);// 接单超时【未接单】
                                kitchenOrdersService.updateOrders(r);
                                //更新买家缓存、钱包、账单
                                mqUtils.sendPurseMQ(r.getMyId(), 16, 0, r.getMoney());
                                //清除缓存中的厨房订单信息
                                redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                                log.info("更新了厨房订单[" + r.getId() + "]操作成功,状态为：接单超时！");
                                arrList.remove(i);
                            } else {
                                continue;
                            }
                        } else if (r.getOrdersType() == 2) {
                            long orderTime = r.getOrderTime().getTime();// 接单时间
                            if (orderTime <= nowTime - countTime6) {
                                r.setOrdersType(8);// 发货超时【未发货】
                                kitchenOrdersService.updateOrders(r);
                                //更新买家缓存、钱包、账单
                                mqUtils.sendPurseMQ(r.getMyId(), 16, 0, r.getMoney());
                                //清除缓存中的厨房订单信息
                                redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                                log.info("更新了厨房订单[" + r.getId() + "]操作成功,状态为：发货超时！");
                                arrList.remove(i);
                            } else {
                                continue;
                            }
                        } else if (r.getOrdersType() == 3) {
                            long deliverTime = r.getDeliveryTime().getTime();// 发货时间
                            if (deliverTime <= nowTime - countTime24) {
                                r.setOrdersType(4);// 收货超时 更新为已收货
                                r.setReceivingTime(new Date());//收货时间
                                kitchenOrdersService.updateOrders(r);
                                //更新买家缓存、钱包、账单
                                mqUtils.sendPurseMQ(r.getMyId(), 16, 0, r.getMoney());
                                //清除缓存中的厨房订单信息
                                redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                                log.info("更新了厨房订单[" + r.getId() + "]操作成功,状态为：收货超时！");
                                arrList.remove(i);
                            } else {
                                continue;
                            }
                        } else if (r.getOrdersType() == 4) {//用户自己确认收货
                            r.setOrdersType(4);// 收货超时 更新为已收货
                            r.setReceivingTime(new Date());//收货时间
                            kitchenOrdersService.updateOrders(r);
                            //更新卖家缓存、钱包、账单
                            mqUtils.sendPurseMQ(r.getUserId(), 16, 0, r.getMoney());
                            //清除缓存中的厨房订单信息
                            redisUtils.expire(Constants.REDIS_KEY_KITCHENORDERS + r.getMyId() + "_" + r.getNo(), 0);
                            log.info("更新了厨房订单[" + r.getId() + "]操作成功,状态为：用户确认收货！");
                            arrList.remove(i);
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
