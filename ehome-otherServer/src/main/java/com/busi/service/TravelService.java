package com.busi.service;

import com.busi.dao.TravelDao;
import com.busi.entity.*;
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
 * @description: 景区相关
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 14:30:06
 */
@Service
public class TravelService {

    @Autowired
    private TravelDao kitchenBookedDao;

    /***
     * 根据用户ID查询预定
     * @param userId
     * @return
     */
    public ScenicSpot findReserve(long userId) {
        return kitchenBookedDao.findReserve(userId);
    }

    /***
     * 新建景区
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addKitchen(ScenicSpot kitchen) {
        return kitchenBookedDao.addKitchen(kitchen);
    }

    /***
     * 更新景区
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen(ScenicSpot kitchen) {
        return kitchenBookedDao.updateKitchen(kitchen);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen3(ScenicSpot kitchen) {
        return kitchenBookedDao.updateKitchen3(kitchen);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen2(ScenicSpot kitchen) {
        return kitchenBookedDao.updateKitchen2(kitchen);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(ScenicSpot kitchen) {
        return kitchenBookedDao.update(kitchen);
    }

    /***
     * 更新景区评分
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateScore(ScenicSpot kitchen) {
        return kitchenBookedDao.updateScore(kitchen);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public ScenicSpot findById(long id) {
        return kitchenBookedDao.findById(id);
    }

    /***
     * 更新景区删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(ScenicSpot kitchen) {
        return kitchenBookedDao.updateDel(kitchen);
    }

    /***
     * 删除景区门票
     * @return
     */
    public int delScenicSpot(long userId, long id) {
        return kitchenBookedDao.delScenicSpot(userId, id);
    }

    /***
     * 更新景区营业状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(ScenicSpot kitchen) {
        return kitchenBookedDao.updateBusiness(kitchen);
    }

    /***
     * 条件查询景区
     * @param watchVideos 筛选视频：0否 1是
     * @param name    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ScenicSpot> findKitchenList(long userId, int watchVideos, String name, int province, int city, int district, double lat, double lon, int page, int count) {

        List<ScenicSpot> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            name = null;
        }
        list = kitchenBookedDao.findKitchenList(userId, watchVideos, name, province, city, district, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增门票
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(ScenicSpotTickets kitchenDishes) {
        return kitchenBookedDao.addDishes(kitchenDishes);
    }

    /***
     * 更新门票
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(ScenicSpotTickets kitchenDishes) {
        return kitchenBookedDao.updateDishes(kitchenDishes);
    }

    /***
     * 删除景区门票
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return kitchenBookedDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询门票
     * @param id
     * @return
     */
    public ScenicSpotTickets disheSdetails(long id) {
        return kitchenBookedDao.disheSdetails(id);
    }

    /***
     * 查询景区门票列表
     * @param kitchenId  景区ID
     * @return
     */
    public List<ScenicSpotTickets> findList(long kitchenId) {
        List<ScenicSpotTickets> list;
        list = kitchenBookedDao.findDishesList(kitchenId);
        return list;
    }

    /***
     * 批量查询指定的门票
     * @param ids
     * @return
     */
    public List<ScenicSpotTickets> findDishesList(String[] ids) {
        List<ScenicSpotTickets> list;
        list = kitchenBookedDao.findDishesList2(ids);
        return list;
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long id) {
        ScenicSpotCollection kitchen = null;
        kitchen = kitchenBookedDao.findWhether(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 新增景区收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollect(ScenicSpotCollection hourlyWorkerCollection) {
        return kitchenBookedDao.addCollect(hourlyWorkerCollection);
    }

    /***
     * 查询景区收藏列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId  用户ID
     * @return
     */
    public PageBean<ScenicSpotCollection> findCollectionList(long userId, int page, int count) {

        List<ScenicSpotCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findCollectionList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除景区收藏
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return kitchenBookedDao.del(ids, userId);
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    public ScenicSpotData findReserveData(long id) {
        return kitchenBookedDao.findReserveData(id);
    }

    /***
     * 根据唯一标识符查询
     * @param uid
     * @return
     */
    public ScenicSpotData findReserveDataId(String uid) {
        return kitchenBookedDao.findReserveDataId(uid);
    }

    /***
     * 更新酒店民宿入驻状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int claimKitchen(ScenicSpotData kitchen) {
        return kitchenBookedDao.claimKitchen(kitchen);
    }

    /***
     * 更新酒店民宿入驻状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int claimKitchen2(ScenicSpot kitchen) {
        return kitchenBookedDao.claimKitchen2(kitchen);
    }

    /***
     * 条件查询酒店民宿
     * @param lat 纬度
     * @param lon 经度
     * @param kitchenName  酒店民宿名字
     * @param page 页码
     * @param count  条数
     * @return
     */
    public PageBean<ScenicSpotData> findReserveDataList(String kitchenName, double lat, double lon, int page, int count) {
        List<ScenicSpotData> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(kitchenName)) {
            kitchenName = null;
        }
        list = kitchenBookedDao.findReserveDataList(kitchenName, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新建酒店民宿
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addReserveData(ScenicSpotData kitchen) {
        return kitchenBookedDao.addReserveData(kitchen);
    }

    /***
     * 更新酒店民宿
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateReserveData(ScenicSpotData kitchen) {
        return kitchenBookedDao.updateReserveData(kitchen);
    }
}
