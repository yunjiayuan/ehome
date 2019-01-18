package com.busi.servive;

import com.busi.dao.UsedDealOrdersDao;
import com.busi.entity.PageBean;
import com.busi.entity.UsedDealExpress;
import com.busi.entity.UsedDealLogistics;
import com.busi.entity.UsedDealOrders;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 二手订单
 * @author: ZHaoJiaJie
 * @create: 2018-10-26 09:55
 */
@Service
public class UsedDealOrdersService {

    @Autowired
    private UsedDealOrdersDao usedDealOrdersDao;

    /***
     * 查询所有二手订单
     * @param //ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时
     * @return
     */
    public List<UsedDealOrders> findOrderList2() {
        List<UsedDealOrders> list;
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
    public int updateCollect(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.updateCollect(usedDealOrders);
    }



    /***
     * 取消订单
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int cancelOrders(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.cancelOrders(usedDealOrders);
    }

}
