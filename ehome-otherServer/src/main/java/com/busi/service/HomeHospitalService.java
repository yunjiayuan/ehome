package com.busi.service;

import com.busi.dao.HomeHospitalDao;
import com.busi.entity.HomeHospital;
import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.PageBean;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 家医馆
 * @author: ZHaoJiaJie
 * @create: 2020-01-07 16:25
 */
@Service
public class HomeHospitalService {

    @Autowired
    private HomeHospitalDao homeHospitalDao;

    /***
     * 新建
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeHospital hourlyWorker) {
        return homeHospitalDao.add(hourlyWorker);
    }

    /***
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeHospital hourlyWorker) {
        return homeHospitalDao.update(hourlyWorker);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update2(HomeHospital hourlyWorker) {
        return homeHospitalDao.update2(hourlyWorker);
    }

    /***
     * 更新营业状态
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(HomeHospital hourlyWorker) {
        return homeHospitalDao.updateBusiness(hourlyWorker);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public HomeHospital findByUserId(long userId) {
        return homeHospitalDao.findByUserId(userId);
    }

    public HomeHospital findById(long id) {
        return homeHospitalDao.findById(id);
    }

    /***
     * 更新删除状态
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(HomeHospital hourlyWorker) {
        return homeHospitalDao.updateDel(hourlyWorker);
    }

    /***
     * 更新帮助人数
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(HomeHospital hourlyWorker) {
        return homeHospitalDao.updateNumber(hourlyWorker);
    }

    /***
     * 查询列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param department  科室
     * @param search    模糊搜索（可以是：症状、疾病、医院、科室、医生名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<HomeHospital> findList(int cityId, int watchVideos, long userId, int department, String search, int province, int city, int district, int page, int count) {

        List<HomeHospital> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (cityId > -1) {
            list = homeHospitalDao.findList4(cityId, userId);
        } else {
            if (!CommonUtils.checkFull(search)) {
                list = homeHospitalDao.findList2(watchVideos, userId, search, province, city, district, department);
            } else {
                list = homeHospitalDao.findList3(watchVideos, userId, province, city, district, department);
            }
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询列表
     * @param users    用户
     * @return
     */
    public List<HomeHospital> findUsersList(String[] users) {

        List<HomeHospital> list = null;
        list = homeHospitalDao.findUsersList(users);
        return list;
    }

    /***
     * 查询等待人员列表(默认第一位是正在咨询中，其余为等待中)
     * @param userId   医师ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<HomeHospitalRecord> findWaitList(long userId, int page, int count) {

        List<HomeHospitalRecord> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeHospitalDao.findWaitList(userId);
        return PageUtils.getPageBean(p, list);
    }
}
