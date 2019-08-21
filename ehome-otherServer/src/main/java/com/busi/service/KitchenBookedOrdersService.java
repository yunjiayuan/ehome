package com.busi.service;

import com.busi.dao.KitchenBookedOrdersDao;
import com.busi.entity.KitchenBookedOrders;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
     * 新增厨房订座订单
     * @param kitchenBookedOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(KitchenBookedOrders kitchenBookedOrders) {
        return kitchenBookedOrdersDao.addOrders(kitchenBookedOrders);
    }

    /***
     * 根据用户ID查询订单
     * @param id
     * @param type  查询场景 0删除 1由未接单改为已接单 2由已接单改为已上桌 3由菜已上桌改为进餐中 4由进餐中改为完成 5加菜 6取消订单
     * @return
     */
    public KitchenBookedOrders findById(long id, long userId, int type) {
        return kitchenBookedOrdersDao.findById(id, userId, type);
    }

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    public KitchenBookedOrders findNo(String no) {
        return kitchenBookedOrdersDao.findByNo(no);
    }

    /***
     *  更新厨房订座订单状态
     *  updateCategory 更新类别  0删除状态  1由未接单改为已接单  2由已接单改为菜已上桌  3由菜已上桌改为进餐中  4更新支付状态  5完成   6取消订单、评价状态
     * @param kitchenBookedOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(KitchenBookedOrders kitchenBookedOrders) {
        return kitchenBookedOrdersDao.updateOrders(kitchenBookedOrders);
    }

    /***
     *  更新厨房订座订单(加菜)
     * @param kitchenBookedOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upOrders(KitchenBookedOrders kitchenBookedOrders) {
        return kitchenBookedOrdersDao.upOrders(kitchenBookedOrders);
    }

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  0全部 1未接单,2已接单,3进餐中，4完成  5退款
     * @return
     */
    public PageBean<KitchenBookedOrders> findOrderList(int identity, long userId, int ordersType, int page, int count) {

        List<KitchenBookedOrders> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedOrdersDao.findOrderList(identity, userId, ordersType);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    public List<KitchenBookedOrders> findIdentity(int identity, long userId) {
        List<KitchenBookedOrders> list;
        list = kitchenBookedOrdersDao.findIdentity(identity, userId);
        return list;
    }

    /***
     * 查询订单
     * @return
     */
    public List<KitchenBookedOrders> findOrdersList(long userId, Date eatTime, int bookedType) {
        List<KitchenBookedOrders> list;
        list = kitchenBookedOrdersDao.findOrdersList(userId,eatTime, bookedType);
        return list;
    }
}
