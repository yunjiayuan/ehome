package com.busi.service;

import com.busi.dao.CashOutDao;
import com.busi.entity.CashOutOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提现相关Service
 * author：SunTianJie
 * create time：2020-7-1 15:06:10
 */
@Service
public class CashOutService {

    @Autowired
    private CashOutDao cashOutDao;

    /***
     * 新增
     * @param cashOutOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCashOutOrder(CashOutOrder cashOutOrder) {
        return cashOutDao.addCashOutOrder(cashOutOrder);
    }

    /***
     * 根据订单ID查询订单信息
     * @param userId
     * @param id
     * @return
     */
    public CashOutOrder findCashOutOrder(long userId, String id) {
        return cashOutDao.findCashOutOrder(userId, id);
    }

    /***
     * 更新支付状态
     * @param cashOutOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateCashOutOrder(CashOutOrder cashOutOrder) {
        return cashOutDao.updateCashOutOrder(cashOutOrder);
    }

    /***
     * 更新到账状态
     * @param cashOutOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateCashOutStatus(CashOutOrder cashOutOrder) {
        return cashOutDao.updateCashOutStatus(cashOutOrder);
    }

}
