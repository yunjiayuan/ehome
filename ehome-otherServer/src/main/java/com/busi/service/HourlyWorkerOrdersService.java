package com.busi.service;

import com.busi.dao.HourlyWorkerOrdersDao;
import com.busi.entity.HourlyWorkerEvaluate;
import com.busi.entity.HourlyWorkerFabulous;
import com.busi.entity.HourlyWorkerOrders;
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
 * @description: 小时工订单
 * @author: ZHaoJiaJie
 * @create: 2019-04-24 17:37
 */
@Service
public class HourlyWorkerOrdersService {

    @Autowired
    private HourlyWorkerOrdersDao hourlyWorkerOrdersDao;

    /***
     * 新增订单
     * @param hourlyWorkerOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(HourlyWorkerOrders hourlyWorkerOrders) {
        return hourlyWorkerOrdersDao.addOrders(hourlyWorkerOrders);
    }

    /***
     * 新增点赞
     * @param like
     * @return
    //     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addLike(HourlyWorkerFabulous like) {
        return hourlyWorkerOrdersDao.addLike(like);
    }

    /***
     * 新增评价
     * @param evaluate
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addEvaluate(HourlyWorkerEvaluate evaluate) {
        return hourlyWorkerOrdersDao.addEvaluate(evaluate);
    }

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
     * 更新厨房订单状态
     * @param hourlyWorkerOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(HourlyWorkerOrders hourlyWorkerOrders) {
        return hourlyWorkerOrdersDao.updateOrders(hourlyWorkerOrders);
    }

    /***
     * 订单管理条件查询
     * @param count       : 每页的显示条数
     * @param page        : 当前查询数据的页码
     * @param identity    : 身份区分：1买家 2商家
     * @param ordersType  : 查询类型:   0已下单未付款  1已付款未接单  ,2已接单未完成,  3已完成未评价 4已评价 5用户取消订单 、 商家取消订单 、 接单超时 、 付款超时
     * @return
     */
    public PageBean<HourlyWorkerOrders> findOrderList(int identity, long userId, int ordersType, int page, int count) {

        List<HourlyWorkerOrders> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        String ids = "3,4,5,7";
        list = hourlyWorkerOrdersDao.findOrderList(identity, userId, ordersType, ids.split(","));

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据订单编号查询订单
     * @param no  订单编号
     * @return
     */
    public HourlyWorkerOrders findByNo(String no) {
        return hourlyWorkerOrdersDao.findByNo(no);
    }

    /***
     * 统计各类订单数量
     * @param identity 身份区分：1买家 2商家
     * @return
     */
    public List<HourlyWorkerOrders> findIdentity(int identity, long userId) {
        List<HourlyWorkerOrders> list;
        list = hourlyWorkerOrdersDao.findIdentity(identity, userId);
        return list;
    }
}
