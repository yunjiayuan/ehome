package com.busi.service;

import com.busi.dao.TalkToSomeonelDao;
import com.busi.entity.PageBean;
import com.busi.entity.TalkToSomeone;
import com.busi.entity.TalkToSomeoneOrder;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 找人倾诉相关接口
 * @author: ZHaoJiaJie
 * @create: 2020-11-23 15:30:21
 */
@Service
public class TalkToSomeoneService {

    @Autowired
    private TalkToSomeonelDao kitchenBookedDao;

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public TalkToSomeone findSomeone(long userId) {
        return kitchenBookedDao.findSomeone(userId);
    }

    /***
     * 根据订单编号查询
     * @param no
     * @return
     */
    public TalkToSomeoneOrder findSomeone2(String no) {
        return kitchenBookedDao.findSomeone2(no);
    }

    /***
     * 新建
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(TalkToSomeone kitchen) {
        return kitchenBookedDao.add(kitchen);
    }

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(TalkToSomeone kitchen) {
        return kitchenBookedDao.update(kitchen);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update2(TalkToSomeone kitchen) {
        return kitchenBookedDao.update2(kitchen);
    }

    /***
     * 新建
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int talkToSomeone(TalkToSomeoneOrder kitchen) {
        return kitchenBookedDao.talkToSomeone(kitchen);
    }

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeSomeoneState(TalkToSomeoneOrder kitchen) {
        return kitchenBookedDao.changeSomeoneState(kitchen);
    }

    /***
     * 更新支付状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(TalkToSomeoneOrder kitchen) {
        return kitchenBookedDao.updateOrders(kitchen);
    }


    /***
     * 查询列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<TalkToSomeone> findSomeoneList(int page, int count) {

        List<TalkToSomeone> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findSomeoneList();

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param type
     * @return
     */
    public PageBean<TalkToSomeoneOrder> findSomeoneHistoryList(long myId, int type, int page, int count) {

        List<TalkToSomeoneOrder> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findSomeoneHistoryList(myId, type);

        return PageUtils.getPageBean(p, list);
    }
}
