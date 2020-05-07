package com.busi.servive;

import com.busi.dao.ShopFloorOrdersDao;
import com.busi.entity.ShopFloorOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 搂店、礼尚往来、合伙购订单
 * @author: ZHaoJiaJie
 * @create: 2018-10-26 09:55
 */
@Service
public class ShopFloorOrdersService {

    @Autowired
    private ShopFloorOrdersDao usedDealOrdersDao;

    /***
     * 查询所有二手订单
     * @param //ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价)
     * @return
     */
    public List<ShopFloorOrders> findOrderList2() {
        List<ShopFloorOrders> list;
        String ids = "0,1,2";
        list = usedDealOrdersDao.findOrderList2(ids.split(","));
        return list;
    }

    /***
     * 更新订单收货状态
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateCollect(ShopFloorOrders usedDealOrders) {
        return usedDealOrdersDao.updateCollect(usedDealOrders);
    }



    /***
     * 取消订单
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int cancelOrders(ShopFloorOrders usedDealOrders) {
        return usedDealOrdersDao.cancelOrders(usedDealOrders);
    }

}
