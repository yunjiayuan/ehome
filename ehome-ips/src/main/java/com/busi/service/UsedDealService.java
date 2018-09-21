package com.busi.service;

import com.busi.dao.UsedDealDao;
import com.busi.entity.PageBean;
import com.busi.entity.UsedDeal;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 二手
 * @author: ZHaoJiaJie
 * @create: 2018-09-18 17:41
 */
@Service
public class UsedDealService {

    @Autowired
    private UsedDealDao usedDealDao;

    /***
     * 新增
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(UsedDeal usedDeal) {
        return usedDealDao.add(usedDeal);
    }

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id, long userId) {
        return usedDealDao.del(id, userId);
    }

    /***
     * 更新
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(UsedDeal usedDeal) {
        return usedDealDao.update(usedDeal);
    }

    /***
     * 更新删除状态
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(UsedDeal usedDeal) {
        return usedDealDao.updateDel(usedDeal);
    }

    /***
     * 更新公告状态
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateStatus(UsedDeal usedDeal) {
        return usedDealDao.updateStatus(usedDeal);
    }

    /***
     * 刷新公告时间
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTime(UsedDeal usedDeal) {
        return usedDealDao.updateTime(usedDeal);
    }

    /***
     * 置顶公告
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setTop(UsedDeal usedDeal) {
        return usedDealDao.setTop(usedDeal);
    }

    /***
     * 统计当月置顶次数
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int statistics(long userId) {
        return usedDealDao.statistics(userId);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public UsedDeal findUserById(long id) {
        return usedDealDao.findUserById(id);
    }

    /***
     * 分页查询
     * @param sort  排序条件:0默认排序，1最新发布，2价格最低，3价格最高，4离我最近
     * @param userId  用户ID
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param usedSort1  一级分类:起始值为0,默认-1为不限 :二手手机 、数码、汽车...
     * @param usedSort2  二级分类:起始值为0,默认-1为不限 : 苹果,三星,联想....
     * @param usedSort3  三级分类:起始值为0,默认-1为不限 :iPhone6s.iPhone5s....
     * @param province  省
     * @param city  市
     * @param district  区
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<UsedDeal> findList(int sort, long userId, int province, int city, int district, int minPrice, int maxPrice, int usedSort1, int usedSort2, int usedSort3, int page, int count) {

        List<UsedDeal> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = usedDealDao.findList(sort, userId, province, city, district, minPrice, maxPrice, usedSort1, usedSort2, usedSort3);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询
     * @param lat  纬度
     * @param lon  经度
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<UsedDeal> findAoList(double lat, double lon, int page, int count) {

        List<UsedDeal> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = usedDealDao.findAoList(lat, lon);

        return PageUtils.getPageBean(p, list);
    }

    public PageBean<UsedDeal> findList(long userId, int sellType, int page, int count) {

        List<UsedDeal> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = usedDealDao.findUList(userId, sellType);

        return PageUtils.getPageBean(p, list);
    }

    //推荐列表用
    public PageBean<UsedDeal> findList(long userId, int page, int count) {

        List<UsedDeal> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = usedDealDao.findHomeList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计已上架,已卖出已下架,我的订单数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId, int type) {
        return usedDealDao.findNum(userId, type);
    }

}
