package com.busi.service;

import com.busi.dao.KitchenOrdersDao;
import com.busi.entity.*;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
     * 新增厨房订单
     * @param kitchenOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(KitchenOrders kitchenOrders) {
        return kitchenOrdersDao.addOrders(kitchenOrders);
    }

    /***
     * 新增菜品点赞
     * @param like
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addLike(KitchenFabulous like) {
        return kitchenOrdersDao.addLike(like);
    }

    /***
     * 新增评价
     * @param evaluate
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addEvaluate(KitchenEvaluate evaluate) {
        return kitchenOrdersDao.addEvaluate(evaluate);
    }

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
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    public KitchenOrders findByNo(String no) {
        return kitchenOrdersDao.findByNo(no);
    }

    /***
     * 根据评价ID查询
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findEvaluateId(long id) {
        return kitchenOrdersDao.findEvaluateId(id);
    }

    /***
     * 更新厨房订单状态
     * @param kitchenOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(KitchenOrders kitchenOrders) {
        return kitchenOrdersDao.updateOrders(kitchenOrders);
    }

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 订单类型:  订单类型:  0未付款（已下单未付款）1未接单(已付款未接单),2制作中(已接单未发货),3配送(已发货未收货),4已卖出(已收货未评价),  5卖家取消订单 6付款超时 7接单超时 8发货超时 9用户取消订单 10 已评价
     * @return
     */
    public PageBean<KitchenOrders> findOrderList(int identity, long userId, int ordersType, int page, int count) {

        List<KitchenOrders> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        String ids = "5,6,7,8,9";
        list = kitchenOrdersDao.findOrderList(identity, userId, ordersType, ids.split(","));

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    public List<KitchenOrders> findIdentity(int identity, long userId) {
        List<KitchenOrders> list;
        list = kitchenOrdersDao.findIdentity(identity, userId);
        return list;
    }
}
