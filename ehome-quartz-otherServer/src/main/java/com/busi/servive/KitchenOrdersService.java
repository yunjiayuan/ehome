package com.busi.servive;

import com.busi.dao.KitchenOrdersDao;
import com.busi.entity.KitchenOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 厨房订单
 * @author: ZHaoJiaJie
 * @create: 2019-03-07 16:28
 */
@Service
public class KitchenOrdersService {

    @Autowired
    private KitchenOrdersDao kitchenOrdersDao;

    /***
     * 根据用户ID查询订单
     * @param id
     * @param type  查询场景 0删除 1由未接单改为制作中 2由制作中改为配送中 3由配送中改为已卖出  4查看订单详情  5取消订单  6评价
     * @return
     */
    public KitchenOrders findById(long id, long userId, int type) {
        return kitchenOrdersDao.findById(id, userId, type);
    }

    /***
     * 更新厨房订单类型
     * @param kitchenOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(KitchenOrders kitchenOrders) {
        return kitchenOrdersDao.updateOrders(kitchenOrders);
    }

    /***
     * 订单条件查询
     * @return
     */
    public List<KitchenOrders> findOrderList() {

        List<KitchenOrders> list;
        String ids = "0,1,2,3";
        list = kitchenOrdersDao.findOrderList(ids.split(","));

        return list;
    }

}
