package com.busi.service;

import com.busi.dao.CashOutDao;
import com.busi.entity.CashOutOrder;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /***
     * 根据用户ID查询提现记录列表
     * @param findType
     * @param page
     * @param count
     * @return
     */
    public PageBean<CashOutOrder> findCashOutList(long findType,int page, int count) {

        List<CashOutOrder> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = cashOutDao.findCashOutList(findType);
        return PageUtils.getPageBean(p, list);
    }

}
