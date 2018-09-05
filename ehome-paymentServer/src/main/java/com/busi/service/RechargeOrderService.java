package com.busi.service;


import com.busi.dao.RechargeOrderDao;
import com.busi.entity.RechargeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 充值订单相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class RechargeOrderService {

    @Autowired
    private RechargeOrderDao rechargeOrderDao;

    /***
     * 新增
     * @param rechargeOrder
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addRechargeOrder( RechargeOrder rechargeOrder){
        return rechargeOrderDao.addRechargeOrder(rechargeOrder);
    }

    /***
     * 根据订单号和用户ID查询
     * @param userId      用户ID
     * @param orderNumber 订单号
     * @return
     */
    public RechargeOrder findRechargeOrder(long userId,String orderNumber){
        return rechargeOrderDao.findRechargeOrder(userId,orderNumber);
    }

    /***
     * 更新支付状态
     * @param userId      用户ID
     * @param orderNumber 订单号
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateRechargeOrder(long userId,String orderNumber){
        return  rechargeOrderDao.updateRechargeOrder(userId,orderNumber);
    }

}
