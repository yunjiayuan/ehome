package com.busi.service;

import com.busi.dao.KitchenBookedDao;
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
 * @description: 厨房订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-27 10:05
 */
@Service
public class KitchenBookedService {

    @Autowired
    private KitchenBookedDao kitchenBookedDao;

    /***
     * 新增厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(KitchenBooked kitchenBooked) {
        return kitchenBookedDao.add(kitchenBooked);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public KitchenBooked findByUserId(long userId) {
        return kitchenBookedDao.findByUserId(userId);
    }

    /***
     * 更新厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBooked(KitchenBooked kitchenBooked) {
        return kitchenBookedDao.updateBooked(kitchenBooked);
    }

    /***
     * 更新厨房订座剩余数量
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePosition(KitchenBooked kitchenBooked) {
        return kitchenBookedDao.updatePosition(kitchenBooked);
    }

    /***
     * 新增包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPrivateRoom(KitchenPrivateRoom kitchenPrivateRoom) {
        return kitchenBookedDao.addPrivateRoom(kitchenPrivateRoom);
    }

    /***
     * 查询包间列表
     * @param userId
     * @return
     */
    public PageBean<KitchenPrivateRoom> findRoomList(long userId, int bookedType, int page, int count) {
        List<KitchenPrivateRoom> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findRoomList(userId, bookedType);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upPrivateRoom(KitchenPrivateRoom kitchenPrivateRoom) {
        return kitchenBookedDao.upPrivateRoom(kitchenPrivateRoom);
    }

    /***
     * 删除包间or大厅
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delPrivateRoom(String[] ids, long userId) {
        return kitchenBookedDao.delPrivateRoom(ids, userId);
    }

    /***
     * 根据ID包间or大厅
     * @param id
     * @return
     */
    public KitchenPrivateRoom findPrivateRoom(long id) {
        return kitchenBookedDao.findPrivateRoom(id);
    }

    /***
     * 根据用户ID查询预定
     * @param userId
     * @return
     */
    public KitchenReserve findReserve(long userId) {
        return kitchenBookedDao.findReserve(userId);
    }

    /***
     * 新建厨房
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addKitchen(KitchenReserve kitchen) {
        return kitchenBookedDao.addKitchen(kitchen);
    }

    /***
     * 更新厨房
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen(KitchenReserve kitchen) {
        return kitchenBookedDao.updateKitchen(kitchen);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public KitchenReserve findById(long id) {
        return kitchenBookedDao.findById(id);
    }

    /***
     * 更新厨房删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(KitchenReserve kitchen) {
        return kitchenBookedDao.updateDel(kitchen);
    }

    /***
     * 更新厨房营业状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(KitchenReserve kitchen) {
        return kitchenBookedDao.updateBusiness(kitchen);
    }

    /***
     * 条件查询厨房
     * @param userId 用户ID
     * @param cuisine    菜系
     * @param lat 纬度
     * @param lon 经度
     * @param kitchenName  厨房名字
     * @param page 页码
     * @param count  条数
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType  排序类型：默认0综合排序  1距离最近  2销量最高  3评分最高
     * @return
     */
    public PageBean<KitchenReserve> findKitchenList(long userId, String cuisine, int watchVideos, int sortType, String kitchenName, double lat, double lon, int page, int count) {

        List<KitchenReserve> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(cuisine)) {
            cuisine = null;
        }
        if (CommonUtils.checkFull(kitchenName)) {
            kitchenName = null;
        }
        if (!CommonUtils.checkFull(kitchenName) || !CommonUtils.checkFull(cuisine)) {
            list = kitchenBookedDao.findKitchenList(userId, watchVideos, kitchenName, cuisine);
        } else if (sortType == 1) {
            list = kitchenBookedDao.findKitchenList2(userId, watchVideos, lat, lon);
        } else {
            list = kitchenBookedDao.findKitchenList3(userId, watchVideos, sortType);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新厨房总销量
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(KitchenReserve kitchen) {
        return kitchenBookedDao.updateNumber(kitchen);
    }

    /***
     * 更新厨房总评分
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateScore(KitchenReserve kitchen) {
        return kitchenBookedDao.updateScore(kitchen);
    }

    /***
     * 新增菜品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(KitchenReserveDishes kitchenDishes) {
        return kitchenBookedDao.addDishes(kitchenDishes);
    }

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(KitchenReserveDishes kitchenDishes) {
        return kitchenBookedDao.updateDishes(kitchenDishes);
    }

    /***
     * 删除厨房菜品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return kitchenBookedDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询菜品
     * @param id
     * @return
     */
    public KitchenReserveDishes disheSdetails(long id) {
        return kitchenBookedDao.disheSdetails(id);
    }

    /***
     * 查询厨房菜品列表
     * @param kitchenId  厨房ID
     * @return
     */
    public List<KitchenReserveDishes> findDishesList2(long kitchenId) {

        List<KitchenReserveDishes> list;
        list = kitchenBookedDao.findDishesList(kitchenId);

        return list;
    }
}
