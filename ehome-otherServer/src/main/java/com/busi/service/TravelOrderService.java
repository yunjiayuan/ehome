package com.busi.service;

import com.busi.dao.TravelOrderDao;
import com.busi.entity.ScenicSpotOrder;
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
 * @description: 家门口旅游订单
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 14:34:18
 */
@Service
public class TravelOrderService {
    @Autowired
    private TravelOrderDao travelOrderDao;

    /***
     * 新增家门口旅游订单
     * @param scenicSpotOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(ScenicSpotOrder scenicSpotOrder) {
        return travelOrderDao.addOrders(scenicSpotOrder);
    }

    /***
     * 根据用户ID查询订单
     * @param id
     * @param type  查询场景 0删除 1由未验票改为已验票 2由已验票改为已完成 3取消订单
     * @return
     */
    public ScenicSpotOrder findById(long id, long userId, int type) {
        return travelOrderDao.findById(id, userId, type);
    }

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    public ScenicSpotOrder findNo(String no) {
        return travelOrderDao.findByNo(no);
    }

    /***
     *  更新家门口旅游订单状态
     *  updateCategory 更新类别  0删除状态  1由未验票改为已验票  2由已验票改为已完成  3更新支付状态  4取消订单、评价状态
     * @param scenicSpotOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(ScenicSpotOrder scenicSpotOrder) {
        return travelOrderDao.updateOrders(scenicSpotOrder);
    }

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型: -1全部 0未付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成未评价  4卖家取消订单 5用户取消订单 6已过期
     * @return
     */
    public PageBean<ScenicSpotOrder> findOrderList(int identity, long userId, int ordersType, int page, int count) {

        List<ScenicSpotOrder> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = travelOrderDao.findOrderList(identity, userId, ordersType);

        return PageUtils.getPageBean(p, list);
    }
}
