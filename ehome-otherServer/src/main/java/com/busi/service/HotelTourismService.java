package com.busi.service;

import com.busi.dao.HotelTourismDao;
import com.busi.entity.*;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 订座设置
 * @author: ZHaoJiaJie
 * @create: 2020-08-13 19:43:22
 */
@Service
public class HotelTourismService {

    @Autowired
    private HotelTourismDao kitchenBookedDao;

    /***
     * 新增厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(KitchenReserveBooked kitchenBooked) {
        return kitchenBookedDao.add(kitchenBooked);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public KitchenReserveBooked findByUserId(long userId, int type) {
        return kitchenBookedDao.findByUserId(userId, type);
    }

    /***
     * 更新厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBooked(KitchenReserveBooked kitchenBooked) {
        return kitchenBookedDao.updateBooked(kitchenBooked);
    }

    /***
     * 更新厨房订座剩余数量
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePosition(KitchenReserveBooked kitchenBooked) {
        return kitchenBookedDao.updatePosition(kitchenBooked);
    }

    /***
     * 新增包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPrivateRoom(KitchenReserveRoom kitchenPrivateRoom) {
        return kitchenBookedDao.addPrivateRoom(kitchenPrivateRoom);
    }

    /***
     * 查询包间列表
     * @param userId
     * @return
     */
    public PageBean<KitchenReserveRoom> findRoomList(int type, long userId, int bookedType, int page, int count) {
        List<KitchenReserveRoom> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findRoomList(type, userId, bookedType);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新包间or大厅
     * @param kitchenPrivateRoom
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upPrivateRoom(KitchenReserveRoom kitchenPrivateRoom) {
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
     * 根据ID查询包间or大厅
     * @param id
     * @return
     */
    public KitchenReserveRoom findPrivateRoom(long id) {
        return kitchenBookedDao.findPrivateRoom(id);
    }

    /***
     * 新增上菜时间
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addUpperTime(KitchenReserveServingTime kitchenDishes) {
        return kitchenBookedDao.addUpperTime(kitchenDishes);
    }

    /***
     * 更新上菜时间
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUpperTime(KitchenReserveServingTime kitchenDishes) {
        return kitchenBookedDao.updateUpperTime(kitchenDishes);
    }

    /***
     * 查询上菜时间
     * @param kitchenId  厨房ID
     * @return
     */
    public KitchenReserveServingTime findUpperTime(long kitchenId, int type) {
        return kitchenBookedDao.findUpperTime(kitchenId, type);
    }

    /***
     * 新增分类
     * @param kitchenDishesSort
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSort(KitchenDishesSort kitchenDishesSort) {
        return kitchenBookedDao.addSort(kitchenDishesSort);
    }

    /***
     * 查询审核列表
     * @param auditType
     * @return
     */
    public PageBean<Hotel> findAuditTypeList(int auditType, double lat, double lon, int page, int count) {
        List<Hotel> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    public PageBean<ScenicSpot> findAuditTypeList2(int auditType, double lat, double lon, int page, int count) {
        List<ScenicSpot> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList2(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    public PageBean<Pharmacy> findAuditTypeList3(int auditType, double lat, double lon, int page, int count) {
        List<Pharmacy> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList3(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    public PageBean<KitchenReserve> findAuditTypeList4(int auditType, double lat, double lon, int page, int count) {
        List<KitchenReserve> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList4(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    public PageBean<Kitchen> findAuditTypeList5(int auditType, double lat, double lon, int page, int count) {
        List<Kitchen> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList5(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    public PageBean<DoorwayBusiness> findAuditTypeList6(int auditType, double lat, double lon, int page, int count) {
        List<DoorwayBusiness> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList6(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    public PageBean<HomeHospital> findAuditTypeList7(int auditType, double lat, double lon, int page, int count) {
        List<HomeHospital> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findAuditTypeList7(auditType, lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据主键ID查询并更新
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAuditType(int type, int auditType, long id) {
        auditType += 1;
        return kitchenBookedDao.changeAuditType(type, auditType, id);
    }

    /***
     * 根据唯一标识查询并更新审核状态
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAuditType2(String claimId) {
        return kitchenBookedDao.changeAuditType2(claimId);
    }

    /***
     * 根据唯一标识查询并更新审核状态
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAuditType3(String claimId) {
        return kitchenBookedDao.changeAuditType3(claimId);
    }

    /***
     * 根据唯一标识查询并更新审核状态
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAuditType4(String claimId) {
        return kitchenBookedDao.changeAuditType4(claimId);
    }

    /***
     * 根据唯一标识查询并更新审核状态
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAuditType5(String claimId) {
        return kitchenBookedDao.changeAuditType5(claimId);
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<Hotel> countAuditType() {
        List<Hotel> list;
        list = kitchenBookedDao.countAuditType();
        return list;
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<ScenicSpot> countAuditType1() {
        List<ScenicSpot> list;
        list = kitchenBookedDao.countAuditType1();
        return list;
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<Pharmacy> countAuditType2() {
        List<Pharmacy> list;
        list = kitchenBookedDao.countAuditType2();
        return list;
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<KitchenReserve> countAuditType3() {
        List<KitchenReserve> list;
        list = kitchenBookedDao.countAuditType3();
        return list;
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<Kitchen> countAuditType4() {
        List<Kitchen> list;
        list = kitchenBookedDao.countAuditType4();
        return list;
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<DoorwayBusiness> countAuditType5() {
        List<DoorwayBusiness> list;
        list = kitchenBookedDao.countAuditType5();
        return list;
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<HomeHospital> countAuditType6() {
        List<HomeHospital> list;
        list = kitchenBookedDao.countAuditType6();
        return list;
    }

    /***
     * 查询此店铺是否还有其他人同时在申请入驻
     * @return
     */
    public List<Hotel> findList(String claimId) {
        List<Hotel> list;
        list = kitchenBookedDao.findList(claimId);
        return list;
    }

    /***
     * 查询此店铺是否还有其他人同时在申请入驻
     * @return
     */
    public List<ScenicSpot> findList2(String claimId) {
        List<ScenicSpot> list;
        list = kitchenBookedDao.findList2(claimId);
        return list;
    }

    /***
     * 查询此店铺是否还有其他人同时在申请入驻
     * @return
     */
    public List<Pharmacy> findList3(String claimId) {
        List<Pharmacy> list;
        list = kitchenBookedDao.findList3(claimId);
        return list;
    }

    /***
     * 查询此店铺是否还有其他人同时在申请入驻
     * @return
     */
    public List<KitchenReserve> findList4(String claimId) {
        List<KitchenReserve> list;
        list = kitchenBookedDao.findList4(claimId);
        return list;
    }
}
