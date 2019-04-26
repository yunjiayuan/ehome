package com.busi.servive;

import com.busi.dao.HourlyWorkerOrdersDao;
import com.busi.entity.HourlyWorkerOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 小时工订单
 * @author: ZHaoJiaJie
 * @create: 2019-04-24 17:37
 */
@Service
public class HourlyWorkerOrdersService {

    @Autowired
    private HourlyWorkerOrdersDao hourlyWorkerOrdersDao;


    /***
     * 根据用户ID查询订单
     * @param id
     * @param type  查询场景 0删除 1由未接单改为已接单 2由服务中改为已完成 3取消订单  4评价
     * @return
     */
    public HourlyWorkerOrders findById(long id, long userId, int type) {
        return hourlyWorkerOrdersDao.findById(id, userId, type);
    }

    /***
     * 更新小时工订单状态
     * @param hourlyWorkerOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(HourlyWorkerOrders hourlyWorkerOrders) {
        return hourlyWorkerOrdersDao.updateOrders(hourlyWorkerOrders);
    }

    /***
     * 订单管理条件查询
     * @return
     */
    public List<HourlyWorkerOrders> findOrderList() {

        List<HourlyWorkerOrders> list;
        String ids = "0,8";
        list = hourlyWorkerOrdersDao.findOrderList(ids.split(","));

        return list;
    }

}
