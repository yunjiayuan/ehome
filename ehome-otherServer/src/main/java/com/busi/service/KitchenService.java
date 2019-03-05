package com.busi.service;

import com.busi.dao.KitchenDao;
import com.busi.entity.Kitchen;
import com.busi.entity.KitchenCollection;
import com.busi.entity.KitchenDishes;
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
 * @description: 厨房
 * @author: ZHaoJiaJie
 * @create: 2019-03-04 15:42
 */
@Service
public class KitchenService {

    @Autowired
    private KitchenDao kitchenDao;

    /***
     * 新建厨房
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addKitchen(Kitchen kitchen) {
        return kitchenDao.addKitchen(kitchen);
    }

    /***
     * 更新厨房
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen(Kitchen kitchen) {
        return kitchenDao.updateKitchen(kitchen);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public Kitchen findByUserId(long userId) {
        return kitchenDao.findByUserId(userId);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public Kitchen findById(long id) {
        return kitchenDao.findById(id);
    }

    /***
     * 更新厨房删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(Kitchen kitchen) {
        return kitchenDao.updateDel(kitchen);
    }

    /***
     * 删除指定厨房下菜品
     * @param id
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int deleteFood(long userId, long id) {
        return kitchenDao.deleteFood(userId, id);
    }

    /***
     * 更新厨房营业状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(Kitchen kitchen) {
        return kitchenDao.updateBusiness(kitchen);
    }

    /***
     * 条件查询厨房
     * @param userId 用户ID
     * @param lat 纬度
     * @param lon 经度
     * @param raidus 半径
     * @param kitchenName  厨房名字
     * @param page 页码
     * @param count  条数
     * @param sortType  排序类型：默认【0综合排序】   0综合排序  1距离最近  2销量最高  3评分最高
     * @return
     */
    public PageBean<Kitchen> findKitchenList(long userId, int sortType, double lat, double lon, int raidus, String kitchenName, int page, int count) {

        List<Kitchen> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(kitchenName)) {
            list = kitchenDao.findKitchenList(userId, kitchenName);
        } else if (sortType == 1) {
            list = kitchenDao.findKitchenList2(userId, raidus, lat, lon);
        } else {
            list = kitchenDao.findKitchenList3(userId, sortType);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long id) {
        KitchenCollection kitchen = null;
        kitchen = kitchenDao.findWhether(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 新增厨房收藏
     * @param kitchenCollection
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollect(KitchenCollection kitchenCollection) {
        return kitchenDao.addCollect(kitchenCollection);
    }

    /***
     * 查询厨房收藏列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId  用户ID
     * @return
     */
    public PageBean<KitchenCollection> findCollectionList(long userId, int page, int count) {

        List<KitchenCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenDao.findCollectionList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 批量查询指定的厨房
     * @param ids
     * @return
     */
    public List<Kitchen> findKitchenList4(String[] ids) {
        List<Kitchen> list;
        list = kitchenDao.findKitchenList4(ids);
        return list;
    }

    /***
     * 删除厨房收藏
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return kitchenDao.del(ids, userId);
    }

    /***
     * 新增菜品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(KitchenDishes kitchenDishes) {
        return kitchenDao.addDishes(kitchenDishes);
    }

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(KitchenDishes kitchenDishes) {
        return kitchenDao.updateDishes(kitchenDishes);
    }

    /***
     * 删除厨房菜品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return kitchenDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询菜品
     * @param id
     * @return
     */
    public KitchenDishes disheSdetails(long id) {
        return kitchenDao.disheSdetails(id);
    }

    /***
     * 查询厨房菜品列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param kitchenId  厨房ID
     * @return
     */
    public PageBean<KitchenDishes> findDishesList(long kitchenId, int page, int count) {

        List<KitchenDishes> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenDao.findDishesList(kitchenId);

        return PageUtils.getPageBean(p, list);
    }
}
