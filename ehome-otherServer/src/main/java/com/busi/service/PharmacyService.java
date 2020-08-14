package com.busi.service;

import com.busi.dao.PharmacyDao;
import com.busi.entity.Pharmacy;
import com.busi.entity.PharmacyCollection;
import com.busi.entity.PharmacyDrugs;
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
 * @description: 药店相关
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 15:51:38
 */
@Service
public class PharmacyService {
    @Autowired
    private PharmacyDao kitchenBookedDao;

    /***
     * 根据用户ID查询预定
     * @param userId
     * @return
     */
    public Pharmacy findReserve(long userId) {
        return kitchenBookedDao.findReserve(userId);
    }

    /***
     * 新建药店
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addKitchen(Pharmacy kitchen) {
        return kitchenBookedDao.addKitchen(kitchen);
    }

    /***
     * 更新药店
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen(Pharmacy kitchen) {
        return kitchenBookedDao.updateKitchen(kitchen);
    }

    /***
     * 更新药店证照
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen2(Pharmacy kitchen) {
        return kitchenBookedDao.updateKitchen2(kitchen);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen3(Pharmacy kitchen) {
        return kitchenBookedDao.updateKitchen3(kitchen);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public Pharmacy findById(long id) {
        return kitchenBookedDao.findById(id);
    }

    /***
     * 更新药店删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(Pharmacy kitchen) {
        return kitchenBookedDao.updateDel(kitchen);
    }

    /***
     * 删除药店药品
     * @return
     */
    public int delPharmacy(long userId, long id) {
        return kitchenBookedDao.delPharmacy(userId, id);
    }

    /***
     * 更新药店营业状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(Pharmacy kitchen) {
        return kitchenBookedDao.updateBusiness(kitchen);
    }

    /***
     * 条件查询药店
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
    public PageBean<Pharmacy> findKitchenList(long userId, int watchVideos, String name, int province, int city, int district, double lat, double lon, int page, int count) {

        List<Pharmacy> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            name = null;
        }
        list = kitchenBookedDao.findKitchenList(userId, watchVideos, name, province, city, district, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增药品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(PharmacyDrugs kitchenDishes) {
        return kitchenBookedDao.addDishes(kitchenDishes);
    }

    /***
     * 更新药品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(PharmacyDrugs kitchenDishes) {
        return kitchenBookedDao.updateDishes(kitchenDishes);
    }

    /***
     * 删除药店药品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return kitchenBookedDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询药品
     * @param id
     * @return
     */
    public PharmacyDrugs disheSdetails(long id) {
        return kitchenBookedDao.disheSdetails(id);
    }

    /***
     * 查询药店药品列表
     * @param kitchenId  药店ID
     * @return
     */
    public PageBean<PharmacyDrugs> findList(long kitchenId, int natureType, int page, int count) {
        List<PharmacyDrugs> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findDishesList(kitchenId, natureType);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询药店药品列表
     * @param kitchenId  药店ID
     * @return
     */
    public List<PharmacyDrugs> findList(long kitchenId) {
        List<PharmacyDrugs> list;
        list = kitchenBookedDao.findDishesList(kitchenId, -1);
        return list;
    }

    /***
     * 批量查询指定的药品
     * @param ids
     * @return
     */
    public List<PharmacyDrugs> findDishesList(String[] ids) {
        List<PharmacyDrugs> list;
        list = kitchenBookedDao.findDishesList2(ids);
        return list;
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long id) {
        PharmacyCollection kitchen = null;
        kitchen = kitchenBookedDao.findWhether(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 新增药店收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollect(PharmacyCollection hourlyWorkerCollection) {
        return kitchenBookedDao.addCollect(hourlyWorkerCollection);
    }

    /***
     * 查询药店收藏列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId  用户ID
     * @return
     */
    public PageBean<PharmacyCollection> findCollectionList(long userId, int page, int count) {

        List<PharmacyCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findCollectionList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除药店收藏
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return kitchenBookedDao.del(ids, userId);
    }
}
