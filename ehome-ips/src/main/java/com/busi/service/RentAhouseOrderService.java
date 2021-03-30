package com.busi.service;

import com.busi.dao.RentAhouseOrderDao;
import com.busi.entity.PageBean;
import com.busi.entity.RentAhouseOrder;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
     * 新增房源
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(RentAhouseOrder kitchenBooked) {
        return kitchenBookedDao.addCommunity(kitchenBooked);
    }

    /***
     * 根据ID查询
     * @param no
     * @return
     */
    public RentAhouseOrder findNo(String no) {
        return kitchenBookedDao.findByUserId(no);
    }

    /***
     * home推荐列表用
     * @param userId   用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<RentAhouseOrder> findOrderList(long userId, int ordersType, int page, int count) {

        List<RentAhouseOrder> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findHList(userId, ordersType);

        return PageUtils.getPageBean(p, list);
    }
}
