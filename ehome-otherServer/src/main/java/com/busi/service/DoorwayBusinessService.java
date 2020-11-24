package com.busi.service;

import com.busi.dao.DoorwayBusinessDao;
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
 * @description: 家门口商家
 * @author: ZhaoJiaJie
 * @create: 2020-11-11 16:57:40
 */
@Service
public class DoorwayBusinessService {

    @Autowired
    private DoorwayBusinessDao kitchenBookedDao;

    /***
     * 根据用户ID查询预定
     * @param userId
     * @return
     */
    public DoorwayBusiness findReserve(long userId) {
        return kitchenBookedDao.findReserve(userId);
    }

    /***
     * 新建商家
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addKitchen(DoorwayBusiness kitchen) {
        return kitchenBookedDao.addKitchen(kitchen);
    }

    /***
     * 更新商家
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen(DoorwayBusiness kitchen) {
        return kitchenBookedDao.updateKitchen(kitchen);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen2(DoorwayBusiness kitchen) {
        return kitchenBookedDao.updateKitchen2(kitchen);
    }

    /***
     * 更新商家评分
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateScore(DoorwayBusiness kitchen) {
        return kitchenBookedDao.updateScore(kitchen);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public DoorwayBusiness findById(long id) {
        return kitchenBookedDao.findById(id);
    }

    /***
     * 更新商家删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(DoorwayBusiness kitchen) {
        return kitchenBookedDao.updateDel(kitchen);
    }

    /***
     * 删除商家商品
     * @return
     */
    public int delDoorwayBusiness(long userId, long id) {
        return kitchenBookedDao.delDoorwayBusiness(userId, id);
    }

    /***
     * 更新商家营业状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(DoorwayBusiness kitchen) {
        return kitchenBookedDao.updateBusiness(kitchen);
    }

    /***
     * 条件查询商家
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
    public PageBean<DoorwayBusiness> findKitchenList(int type, long userId, int watchVideos, String name, int province, int city, int district, double lat, double lon, int page, int count) {

        List<DoorwayBusiness> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            name = null;
        }
        list = kitchenBookedDao.findKitchenList(type, userId, watchVideos, name, province, city, district, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增商品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(DoorwayBusinessCommodity kitchenDishes) {
        return kitchenBookedDao.addDishes(kitchenDishes);
    }

    /***
     * 更新商品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(DoorwayBusinessCommodity kitchenDishes) {
        return kitchenBookedDao.updateDishes(kitchenDishes);
    }

    /***
     * 删除商家商品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return kitchenBookedDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询商品
     * @param id
     * @return
     */
    public DoorwayBusinessCommodity disheSdetails(long id) {
        return kitchenBookedDao.disheSdetails(id);
    }

    /***
     * 查询商家商品列表
     * @param kitchenId  商家ID
     * @return
     */
    public List<DoorwayBusinessCommodity> findList(long kitchenId) {
        List<DoorwayBusinessCommodity> list;
        list = kitchenBookedDao.findDishesList(kitchenId);
        return list;
    }

    /***
     * 批量查询指定的商品
     * @param ids
     * @return
     */
    public List<DoorwayBusinessCommodity> findDishesList(String[] ids) {
        List<DoorwayBusinessCommodity> list;
        list = kitchenBookedDao.findDishesList2(ids);
        return list;
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long id) {
        DoorwayBusinessCollection kitchen = null;
        kitchen = kitchenBookedDao.findWhether(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether2(long userId, long id) {
        DoorwayBusinessCollection kitchen = null;
        kitchen = kitchenBookedDao.findWhether2(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 新增商家收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollect(DoorwayBusinessCollection hourlyWorkerCollection) {
        return kitchenBookedDao.addCollect(hourlyWorkerCollection);
    }

    /***
     * 查询商家收藏列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId  用户ID
     * @return
     */
    public PageBean<DoorwayBusinessCollection> findCollectionList(long userId, int page, int count) {

        List<DoorwayBusinessCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findCollectionList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除商家收藏
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return kitchenBookedDao.del(ids, userId);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen3(DoorwayBusiness kitchen) {
        return kitchenBookedDao.updateKitchen3(kitchen);
    }
}
