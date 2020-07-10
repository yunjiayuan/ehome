package com.busi.servive;

import com.busi.dao.CashOutDao;
import com.busi.entity.CashOutOrder;
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
     * 查询未到账订单
     * @return
     */
    public List<CashOutOrder> findCashOutOrderList() {
        List<CashOutOrder> list;
        list = cashOutDao.findCashOutOrderList();
        return list;
    }

}
