package com.busi.servive;

import com.busi.dao.KitchenBookedOrdersDao;
import com.busi.entity.KitchenBookedOrders;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 厨房订座订单
 * @author: ZHaoJiaJie
 * @create: 2019-06-27 14:55
 */
@Service
public class KitchenBookedOrdersService {

    @Autowired
    private KitchenBookedOrdersDao kitchenBookedOrdersDao;

    /***
     * 订单查询
     * @return
     */
    public List<KitchenBookedOrders> findOrderList() {

        List<KitchenBookedOrders> list;
        String ids = "0,1,2";
        list = kitchenBookedOrdersDao.findOrderList(ids.split(","));

        return list;
    }

    /***
     * 更新厨房订单类型
     * @param kitchenOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(KitchenBookedOrders kitchenOrders) {
        return kitchenBookedOrdersDao.updateOrders(kitchenOrders);
    }

}
