package com.busi.service;

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
     * 新增订单
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addOrders(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.addOrders(usedDealOrders);
    }

    /***
     * 新增物流
     * @param usedDealLogistics
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addLogistics(UsedDealLogistics usedDealLogistics) {
        return usedDealOrdersDao.addLogistics(usedDealLogistics);
    }

    /***
     * 删除订单
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delOrders(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.delOrders(usedDealOrders);
    }

    /***
     * 根据编号查询订单(详情)
     * @param id
     * @return
     */
    public UsedDealOrders findDetailsOrId(String id) {
        return usedDealOrdersDao.findDetailsOrId(id);
    }

    /***
     * 根据ID查询订单(删除订单)
     * @param id
     * @return
     */
    public UsedDealOrders findDelOrId(long id) {
        return usedDealOrdersDao.findDelOrId(id);
    }

    /***
     * 根据ID查询订单(更改发货状态)
     * @param id
     * @return
     */
    public UsedDealOrders findDeliverOrId(long id) {
        return usedDealOrdersDao.findDeliverOrId(id);
    }

    /***
     * 根据ID查询订单(更改收货状态)
     * @param id
     * @return
     */
    public UsedDealOrders findReceiptOrId(long id) {
        return usedDealOrdersDao.findReceiptOrId(id);
    }

    /***
     * 根据ID查询订单(取消订单)
     * @param id
     * @return
     */
    public UsedDealOrders findCancelOrId(long id) {
        return usedDealOrdersDao.findCancelOrId(id);
    }

    /***
     * 根据ID查询物流
     * @param id
     * @return
     */
    public UsedDealLogistics findLogistics(long id) {
        return usedDealOrdersDao.findLogistics(id);
    }

    /***
     * 更新物流
     * @param usedDealLogistics
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateLogistics(UsedDealLogistics usedDealLogistics) {
        return usedDealOrdersDao.updateLogistics(usedDealLogistics);
    }

    /***
     * 更新物流信息
     * @param usedDealLogistics
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateLogisticsData(UsedDealLogistics usedDealLogistics) {
        return usedDealOrdersDao.updateLogisticsData(usedDealLogistics);
    }

    /***
     * 更新订单发货状态
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDelivery(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.updateDelivery(usedDealOrders);
    }

    /***
     * 更新订单付款状态
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePayType(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.updatePayType(usedDealOrders);
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
     * 分页查询二手订单列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity  身份区分：1买家 2商家
     * @param ordersType 订单类型: -1默认全部 0待付款(未付款),1待发货(已付款未发货),2待收货(已发货未收货),3待评价(已收货未评价), 4用户取消订单  5卖家取消订单  6付款超时
     * @return
     */
    public PageBean<UsedDealOrders> findOrderList(int identity, long userId, int ordersType, int page, int count) {

        List<UsedDealOrders> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        String ids = "4,5,6,7";
        list = usedDealOrdersDao.findOrderList(identity, userId, ordersType, ids.split(","));

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 延长订单收货时间
     * @param usedDealOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int timeExpand(UsedDealOrders usedDealOrders) {
        return usedDealOrdersDao.timeExpand(usedDealOrders);
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

    /***
     * 统计各类订单数量
     * @param identity  身份区分：1买家 2商家
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(int identity, int type, long userId) {
        return usedDealOrdersDao.findNum(identity, type, userId);
    }

    /***
     * 判断该用户快递个数是否达到上限  最多10条
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findExpressNum(long userId) {
        return usedDealOrdersDao.findExpressNum(userId);
    }


    /***
     * 新增快递
     * @param usedDealExpress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addExpress(UsedDealExpress usedDealExpress) {
        return usedDealOrdersDao.addExpress(usedDealExpress);
    }

    /***
     * 更新快递
     * @param usedDealExpress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateExpress(UsedDealExpress usedDealExpress) {
        return usedDealOrdersDao.updateExpress(usedDealExpress);
    }

    /***
     * 更新快递删除状态
     * @param usedDealExpress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delExpress(UsedDealExpress usedDealExpress) {
        return usedDealOrdersDao.delExpress(usedDealExpress);
    }

    /***
     * 根据ID查询快递
     * @param id
     * @return
     */
    public UsedDealExpress findExpress(long id) {
        return usedDealOrdersDao.findExpress(id);
    }

    /***
     * 分页查询快递列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @return
     */
    public PageBean<UsedDealExpress> findExpressList(long userId, int page, int count) {

        List<UsedDealExpress> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = usedDealOrdersDao.findExpressList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据ID查询物流
     * @param id
     * @return
     */
    public UsedDealLogistics logisticsDetails(long id) {
        return usedDealOrdersDao.logisticsDetails(id);
    }

}
