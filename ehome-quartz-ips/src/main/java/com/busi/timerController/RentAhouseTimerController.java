package com.busi.timerController;

import com.busi.entity.RentAhouse;
import com.busi.entity.RentAhouseOrder;
import com.busi.servive.RentAhouseOrderService;
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
 * @description: 租房买房
 * @author: ZhaoJiaJie
 * @create: 2021-04-01 12:07:03
 */
@Slf4j
@Component
public class RentAhouseTimerController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RentAhouseOrderService rentAhouseOrderService;

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
    @Scheduled(cron = "0 10 2 * * ?") //2点十分
    public void rentAhouseTimer() throws Exception {
        log.info("开始查询数据库中待处理的租房买房订单...");
        while (true) {
            int tradeType = 0;
            List arrList = null;
            RentAhouseOrder r = null;
            int countTime15 = 15 * 60 * 1000;// 15分钟
            long nowTime = new Date().getTime();// 系统时间
            arrList = rentAhouseOrderService.findOrderList();
            if (arrList != null && arrList.size() > 0) {
                for (int i = 0; i < arrList.size(); i++) {
                    r = (RentAhouseOrder) arrList.get(i);
                    if (r != null) {
                        if (r.getPaymentStatus() == 0) {//未付款
                            long sendTime = r.getAddTime().getTime();// 下单时间
                            if (sendTime <= nowTime - countTime15) {
                                r.setPaymentStatus(2);// 付款超时【已过期】
                                rentAhouseOrderService.updatePayType(r);
                                //更新房源状态为出租中
                                RentAhouse sa = new RentAhouse();
                                sa.setSellState(0);
                                sa.setId(r.getHouseId());
                                rentAhouseOrderService.changeCommunityState(sa);
                                //清除缓存中的信息
                                redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE + sa.getId(), 0);
                                //清除缓存中的订单信息
                                redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE_ORDER + r.getNo(), 0);
                                log.info("更新租房订单[" + r.getId() + "]操作成功,状态为：已过期！");
                            } else {
                                continue;
                            }
                        } else {//已付款
                            if (r.getRoomState() == 0) {
                                tradeType = 49;
                            }
                            if (r.getRoomState() == 1) {
                                tradeType = 47;
                            }
                            //更新房主缓存、钱包、账单
                            mqUtils.sendPurseMQ(r.getMyId(), tradeType, 0, r.getPrice() - r.getDeposit());
                            rentAhouseOrderService.upOrders(r);
                            //清除缓存中的订单信息
                            redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE_ORDER + r.getNo(), 0);
                            log.info("更新租房买房订单[" + r.getId() + "]操作成功,状态为：打款成功！");
                        }
                    }
                }
            }
        }
    }
}
