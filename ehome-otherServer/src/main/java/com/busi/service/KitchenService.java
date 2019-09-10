package com.busi.service;

import com.busi.dao.KitchenDao;
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
     * 更新厨房总评分
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateScore(Kitchen kitchen) {
        return kitchenDao.updateScore(kitchen);
    }

    /***
     * 更新厨房总销量
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(Kitchen kitchen) {
        return kitchenDao.updateNumber(kitchen);
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
     * 更新厨房订座状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBookedState(Kitchen kitchen) {
        return kitchenDao.updateBookedState(kitchen);
    }

    /***
     * 条件查询厨房
     * @param userId 用户ID
     * @param lat 纬度
     * @param lon 经度
    //     * @param raidus 半径
     * @param kitchenName  厨房名字
     * @param page 页码
     * @param count  条数
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType  排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高  4视频
     * @return
     */
//    public PageBean<Kitchen> findKitchenList(long userId, int watchVideos, int sortType, double lat, double lon, int raidus, String kitchenName, int page, int count) {
//
//        List<Kitchen> list;
//        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
//        if (!CommonUtils.checkFull(kitchenName)) {
//            list = kitchenDao.findKitchenList(userId, watchVideos, kitchenName);
//        } else if (sortType == 1) {
//            list = kitchenDao.findKitchenList2(userId, watchVideos, raidus, lat, lon);
//        } else {
//            list = kitchenDao.findKitchenList3(userId, watchVideos, sortType);
//        }
//        return PageUtils.getPageBean(p, list);
//    }
    public PageBean<Kitchen> findKitchenList(long userId, int watchVideos, int sortType, String kitchenName, double lat, double lon, int page, int count) {

        List<Kitchen> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(kitchenName)) {
            list = kitchenDao.findKitchenList(userId, watchVideos, kitchenName);
        } else if (sortType == 1) {
            list = kitchenDao.findKitchenList2(userId, watchVideos, lat, lon);
        } else {
            list = kitchenDao.findKitchenList3(userId, watchVideos, sortType);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long kitchend, int bookedState) {
        KitchenCollection kitchen = null;
        kitchen = kitchenDao.findWhether(userId, kitchend, bookedState);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 验证用户是否收藏过
     * @param kitchenId
     * @return
     */
    public boolean findWhether2(int bookedState, long userId, long kitchenId) {
        KitchenCollection kitchen = null;
        kitchen = kitchenDao.findWhether2(bookedState, userId, kitchenId);
        if (kitchen == null) {
            return false;
        } else {
            return true;
        }
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
    public PageBean<KitchenCollection> findCollectionList(long userId, int bookedState, int page, int count) {

        List<KitchenCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenDao.findCollectionList(userId, bookedState);

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
     * 批量查询指定的厨房
     * @param ids
     * @return
     */
    public List<KitchenReserve> findKitchenList5(String[] ids) {
        List<KitchenReserve> list;
        list = kitchenDao.findKitchenList5(ids);
        return list;
    }

    /***
     * 批量查询指定的菜品
     * @param ids
     * @return
     */
    public List<KitchenDishes> findDishesList(String[] ids) {
        List<KitchenDishes> list;
        list = kitchenDao.findDishesList2(ids);
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
    public PageBean<KitchenDishes> findDishesList(int bookedState, long kitchenId, int page, int count) {

        List<KitchenDishes> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenDao.findDishesList(bookedState, kitchenId);

        return PageUtils.getPageBean(p, list);
    }

    public List<KitchenDishes> findDishesList2(int bookedState, long kitchenId) {

        List<KitchenDishes> list;
        list = kitchenDao.findDishesList(bookedState, kitchenId);

        return list;
    }

    /***
     * 更新菜品点赞数
     * @param dishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateLike(KitchenDishes dishes) {
        return kitchenDao.updateLike(dishes);
    }

    /***
     * 新增分类
     * @param kitchenDishesSort
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSort(KitchenDishesSort kitchenDishesSort) {
        return kitchenDao.addSort(kitchenDishesSort);
    }

    /***
     * 更新分类
     * @param kitchenDishesSort
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishesSort(KitchenDishesSort kitchenDishesSort) {
        return kitchenDao.updateDishesSort(kitchenDishesSort);
    }

    /***
     * 删除厨房菜品分类
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delFoodSort(String[] ids, long userId) {
        return kitchenDao.delFoodSort(ids, userId);
    }

    /***
     * 根据ID查询菜品分类
     * @param id
     * @return
     */
    public KitchenDishesSort findDishesSort(long id) {
        return kitchenDao.findDishesSort(id);
    }

    /***
     * 统计该用户分类数量
     * @param bookedState
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(int bookedState, long kitchenId) {
        return kitchenDao.findNum(bookedState, kitchenId);
    }

    /***
     * 查询厨房菜品分类列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param kitchenId  厨房ID
     * @return
     */
    public PageBean<KitchenDishesSort> findDishesSortList(int bookedState, long kitchenId, int page, int count) {

        List<KitchenDishesSort> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenDao.findDishesSortList(bookedState, kitchenId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询厨房菜品分类列表
     * @param kitchenId  厨房ID
     * @return
     */
    public List<KitchenDishesSort> findDishesSortList2(int bookedState, long kitchenId) {

        List<KitchenDishesSort> list;
        list = kitchenDao.findDishesSortList(bookedState, kitchenId);

        return list;
    }
}
