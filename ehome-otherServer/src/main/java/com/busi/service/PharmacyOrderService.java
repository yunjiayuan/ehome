package com.busi.service;

import com.busi.dao.PharmacyOrderDao;
import com.busi.entity.PharmacyOrder;
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
 * @description: 家门口药店订单相关
 * @author: ZhaoJiaJie
 * @create: 2020-08-11 18:22:04
 */
@Service
public class PharmacyOrderService {

    @Autowired
    private PharmacyOrderDao travelOrderDao;

    /***
     * 新增家门口药店订单
     * @param scenicSpotOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(PharmacyOrder scenicSpotOrder) {
        return travelOrderDao.addOrders(scenicSpotOrder);
    }

    /***
     * 根据用户ID查询订单
     * @param id
     * @param type  查询场景 0删除 1接单 2配送 3验票 4完成 5取消订单
     * @return
     */
    public PharmacyOrder findById(long id, long userId, int type) {
        return travelOrderDao.findById(id, userId, type);
    }

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    public PharmacyOrder findNo(String no) {
        return travelOrderDao.findByNo(no);
    }

    /***
     *  更新家门口药店订单状态
     *  updateCategory 更新类别  0删除状态  1待配送  2配送中  3已送达 4更新支付状态  5取消订单、评价状态  6验证状态
     * @param scenicSpotOrder
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(PharmacyOrder scenicSpotOrder) {
        return travelOrderDao.updateOrders(scenicSpotOrder);
    }

    /***
     * 订单管理条件查询
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型: -1全部 0待支付 1待验证, 2待评价
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @return
     */
    public PageBean<PharmacyOrder> findOrderList(int identity, long userId, int ordersType, int page, int count) {

        List<PharmacyOrder> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = travelOrderDao.findOrderList(identity, userId, ordersType);

        return PageUtils.getPageBean(p, list);
    }
}
