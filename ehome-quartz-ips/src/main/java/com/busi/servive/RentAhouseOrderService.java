package com.busi.servive;

import com.busi.dao.RentAhouseOrderDao;
import com.busi.entity.RentAhouse;
import com.busi.entity.RentAhouseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 租房买房
 * @author: ZhaoJiaJie
 * @create: 2021-03-30 14:11:55
 */
@Service
public class RentAhouseOrderService {

    @Autowired
    private RentAhouseOrderDao kitchenBookedDao;

    /***
     * 订单条件查询
     * @return
     */
    public List<RentAhouseOrder> findOrderList() {

        List<RentAhouseOrder> list;
        list = kitchenBookedDao.findOrderList();

        return list;
    }

    /***
     * 更新订单
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upOrders(RentAhouseOrder kitchenBooked) {
        return kitchenBookedDao.upOrders(kitchenBooked);
    }

    /***
     * 更新订单付款状态
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePayType(RentAhouseOrder usedDealOrders) {
        return kitchenBookedDao.updatePayType(usedDealOrders);
    }

    /***
     * 更新房源状态
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunityState(RentAhouse kitchenBooked) {
        return kitchenBookedDao.changeCommunityState(kitchenBooked);
    }
}
