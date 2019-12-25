package com.busi.service;

import com.busi.dao.ShopFloorOrdersDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShopFloorOrders;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店订单
 * @author: ZHaoJiaJie
 * @create: 2019-12-18 15:44
 */
@Service
public class ShopFloorOrdersService {

    @Autowired
    private ShopFloorOrdersDao shopFloorOrdersDao;

    /***
     * 新增楼店订座订单
     * @param kitchenBookedOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(ShopFloorOrders kitchenBookedOrders) {
        return shopFloorOrdersDao.addOrders(kitchenBookedOrders);
    }

    /***
     * 根据用户ID查询订单
     * @param id
     * @param type  查询场景 0删除 1由未发货改为已发货 2由未收货改为已收货 3取消订单
     * @return
     */
    public ShopFloorOrders findById(long id, long userId, int type) {
        return shopFloorOrdersDao.findById(id, userId, type);
    }

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    public ShopFloorOrders findNo(String no) {
        return shopFloorOrdersDao.findByNo(no);
    }

    /***
     *  更新楼店订单状态
     *  updateCategory 更新类别  0删除状态  1由未发货改为已发货 2由未收货改为已收货 3取消订单 4更新支付状态
     * @param kitchenBookedOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(ShopFloorOrders kitchenBookedOrders) {
        return shopFloorOrdersDao.updateOrders(kitchenBookedOrders);
    }

    /***
     * 分页查询订单列表
     * @param ordersType 订单类型: 0全部 1待付款,2待发货(已付款),3已发货（待收货）, 4已收货（待评价）  5已评价  6付款超时  7发货超时, 8取消订单
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<ShopFloorOrders> findOrderList(long userId, int ordersType, int page, int count) {

        List<ShopFloorOrders> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopFloorOrdersDao.findOrderList(userId, ordersType);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计各类订单数量
     * @return
     */
    public List<ShopFloorOrders> findIdentity(long userId) {
        List<ShopFloorOrders> list;
        list = shopFloorOrdersDao.findIdentity(userId);
        return list;
    }
}
