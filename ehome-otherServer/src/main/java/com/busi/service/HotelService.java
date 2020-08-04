package com.busi.service;

import com.busi.dao.HotelDao;
import com.busi.entity.PageBean;
import com.busi.entity.Hotel;
import com.busi.entity.HotelCollection;
import com.busi.entity.HotelRoom;
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
 * @description: 酒店民宿相关
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 15:51:38
 */
@Service
public class HotelService {
    @Autowired
    private HotelDao kitchenBookedDao;

    /***
     * 根据用户ID查询预定
     * @param userId
     * @return
     */
    public Hotel findReserve(long userId) {
        return kitchenBookedDao.findReserve(userId);
    }

    /***
     * 新建酒店民宿
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addKitchen(Hotel kitchen) {
        return kitchenBookedDao.addKitchen(kitchen);
    }

    /***
     * 更新酒店民宿
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen(Hotel kitchen) {
        return kitchenBookedDao.updateKitchen(kitchen);
    }

    /***
     * 更新酒店民宿证照
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateKitchen2(Hotel kitchen) {
        return kitchenBookedDao.updateKitchen2(kitchen);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public Hotel findById(long id) {
        return kitchenBookedDao.findById(id);
    }

    /***
     * 更新酒店民宿删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(Hotel kitchen) {
        return kitchenBookedDao.updateDel(kitchen);
    }

    /***
     * 删除酒店民宿房间
     * @return
     */
    public int delHotel(long userId, long id) {
        return kitchenBookedDao.delHotel(userId, id);
    }

    /***
     * 更新酒店民宿营业状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(Hotel kitchen) {
        return kitchenBookedDao.updateBusiness(kitchen);
    }

    /***
     * 条件查询酒店民宿
     * @param watchVideos 筛选视频：0否 1是
     * @param hotelType 筛选：-1全部 0酒店 1民宿
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
    public PageBean<Hotel> findKitchenList(long userId, int hotelType, int watchVideos, String name, int province, int city, int district, double lat, double lon, int page, int count) {

        List<Hotel> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            name = null;
        }
        list = kitchenBookedDao.findKitchenList(userId, hotelType, watchVideos, name, province, city, district, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增房间
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDishes(HotelRoom kitchenDishes) {
        return kitchenBookedDao.addDishes(kitchenDishes);
    }

    /***
     * 更新房间
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDishes(HotelRoom kitchenDishes) {
        return kitchenBookedDao.updateDishes(kitchenDishes);
    }

    /***
     * 删除酒店民宿房间
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delDishes(String[] ids, long userId) {
        return kitchenBookedDao.delDishes(ids, userId);
    }

    /***
     * 根据ID查询房间
     * @param id
     * @return
     */
    public HotelRoom disheSdetails(long id) {
        return kitchenBookedDao.disheSdetails(id);
    }

    /***
     * 查询酒店民宿房间列表
     * @param kitchenId  酒店民宿ID
     * @return
     */
    public List<HotelRoom> findList(long kitchenId) {
        List<HotelRoom> list;
        list = kitchenBookedDao.findDishesList(kitchenId);
        return list;
    }

    /***
     * 批量查询指定的房间
     * @param ids
     * @return
     */
    public List<HotelRoom> findDishesList(String[] ids) {
        List<HotelRoom> list;
        list = kitchenBookedDao.findDishesList2(ids);
        return list;
    }

    /***
     * 验证用户是否收藏过
     * @param userId
     * @return
     */
    public boolean findWhether(long userId, long id) {
        HotelCollection kitchen = null;
        kitchen = kitchenBookedDao.findWhether(userId, id);
        if (kitchen == null) {
            return false;
        }
        return true;
    }

    /***
     * 新增酒店民宿收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollect(HotelCollection hourlyWorkerCollection) {
        return kitchenBookedDao.addCollect(hourlyWorkerCollection);
    }

    /***
     * 查询酒店民宿收藏列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId  用户ID
     * @return
     */
    public PageBean<HotelCollection> findCollectionList(long userId, int page, int count) {

        List<HotelCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findCollectionList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除酒店民宿收藏
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return kitchenBookedDao.del(ids, userId);
    }
}
