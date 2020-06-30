package com.busi.service;

import com.busi.dao.HourlyWorkerDao;
import com.busi.entity.HourlyWorker;
import com.busi.entity.HourlyWorkerCollection;
import com.busi.entity.HourlyWorkerType;
import com.busi.entity.PageBean;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 小时工
 * @author: ZHaoJiaJie
 * @create: 2019-04-23 13:51
 */
@Service
public class HourlyWorkerService {

    @Autowired
    private HourlyWorkerDao hourlyWorkerDao;

    /***
     * 新建小时工
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addHourly(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.addHourly(hourlyWorker);
    }

    /***
     * 更新小时工
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateHourly(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.updateHourly(hourlyWorker);
    }

    /***
     * 更新小时工总服务次数
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.updateNumber(hourlyWorker);
    }

    /***
     * 更新小时工总评分
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateScore(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.updateScore(hourlyWorker);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public HourlyWorker findByUserId(long userId) {
        return hourlyWorkerDao.findByUserId(userId);
    }

    /***
     * 更新小时工删除状态
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.updateDel(hourlyWorker);
    }

    /***
     * 更新小时工营业状态
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.updateBusiness(hourlyWorker);
    }

    /***
     * 条件查询小时工
     * @param userId 用户ID
     * @param lat 纬度
     * @param lon 经度
    //     * @param raidus 半径
     * @param name  小时工名字
     * @param page 页码
     * @param count  条数
     * @param sortType  排序类型：默认【0综合排序】   0综合排序  1距离最近  2服务次数最高  3评分最高
     * @return
     */
    public PageBean<HourlyWorker> findHourlyList(long userId, int watchVideos, int sortType, double lat, double lon, String name, int page, int count) {

        List<HourlyWorker> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(name)) {
            list = hourlyWorkerDao.findHourlyList(userId, watchVideos, name);
        } else if (sortType == 1) {
            list = hourlyWorkerDao.findHourlyList2(userId, watchVideos, lat, lon);
        } else {
            list = hourlyWorkerDao.findHourlyList3(userId, watchVideos, sortType, lat, lon);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新小时工实名信息
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateRealName(HourlyWorker hourlyWorker) {
        return hourlyWorkerDao.updateRealName(hourlyWorker);
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long id) {
        HourlyWorkerCollection kitchen = null;
        kitchen = hourlyWorkerDao.findWhether(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 新增小时工收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollect(HourlyWorkerCollection hourlyWorkerCollection) {
        return hourlyWorkerDao.addCollect(hourlyWorkerCollection);
    }

    /***
     * 查询小时工收藏列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId  用户ID
     * @return
     */
    public PageBean<HourlyWorkerCollection> findCollectionList(long userId, int page, int count) {

        List<HourlyWorkerCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = hourlyWorkerDao.findCollectionList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 批量查询指定的小时工
     * @param ids
     * @return
     */
    public List<HourlyWorker> findKitchenList4(String[] ids) {
        List<HourlyWorker> list;
        list = hourlyWorkerDao.findKitchenList4(ids);
        return list;
    }

    /***
     * 删除小时工收藏
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return hourlyWorkerDao.del(ids, userId);
    }

    /***
     * 新增工作类型
     * @param hourlyWorkerType
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(HourlyWorkerType hourlyWorkerType) {
        return hourlyWorkerDao.addDishes(hourlyWorkerType);
    }

    /***
     * 更新工作类型
     * @param hourlyWorkerType
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(HourlyWorkerType hourlyWorkerType) {
        return hourlyWorkerDao.updateDishes(hourlyWorkerType);
    }

    /***
     * 更新工作类型服务次数
     * @param hourlyWorkerType
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateType(HourlyWorkerType hourlyWorkerType) {
        return hourlyWorkerDao.updateType(hourlyWorkerType);
    }

    /***
     * 更新工作类型点赞数
     * @param hourlyWorkerType
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateLike(HourlyWorkerType hourlyWorkerType) {
        return hourlyWorkerDao.updateLike(hourlyWorkerType);
    }

    /***
     * 删除工作类型
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return hourlyWorkerDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询工作类型
     * @param id
     * @return
     */
    public HourlyWorkerType disheSdetails(long id) {
        return hourlyWorkerDao.disheSdetails(id);
    }

    /***
     * 查询工作类型列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param workerId  小时工ID
     * @return
     */
    public PageBean<HourlyWorkerType> findDishesList(long workerId, int page, int count) {

        List<HourlyWorkerType> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = hourlyWorkerDao.findDishesList(workerId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计该用户工作类型数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return hourlyWorkerDao.findNum(userId);
    }

    /***
     * 批量查询指定的工作类型
     * @param ids
     * @return
     */
    public List<HourlyWorkerType> findDishesList(String[] ids) {
        List<HourlyWorkerType> list;
        list = hourlyWorkerDao.findDishesList2(ids);
        return list;
    }
}
