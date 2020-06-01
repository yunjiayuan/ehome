package com.busi.service;

import com.busi.dao.LawyerCircleDao;
import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.LawyerCircle;
import com.busi.entity.LawyerCircleRecord;
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
 * @description: 律师圈
 * @author: ZHaoJiaJie
 * @create: 2020-03-03 18:57:26
 */
@Service
public class LawyerCircleService {
    @Autowired
    private LawyerCircleDao lawyerCircleDao;

    /***
     * 新建
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(LawyerCircle lawyerCircle) {
        return lawyerCircleDao.add(lawyerCircle);
    }

    /***
     * 更新支付状态
     * @param hourlyWorkerOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(LawyerCircleRecord hourlyWorkerOrders) {
        return lawyerCircleDao.updateOrders(hourlyWorkerOrders);
    }

    /***
     * 更新
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(LawyerCircle lawyerCircle) {
        return lawyerCircleDao.update(lawyerCircle);
    }

    /***
     * 更新营业状态
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(LawyerCircle lawyerCircle) {
        return lawyerCircleDao.updateBusiness(lawyerCircle);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public LawyerCircle findByUserId(long userId) {
        return lawyerCircleDao.findByUserId(userId);
    }

    /***
     * 更新删除状态
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(LawyerCircle lawyerCircle) {
        return lawyerCircleDao.updateDel(lawyerCircle);
    }

    /***
     * 更新帮助人数
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(LawyerCircle lawyerCircle) {
        return lawyerCircleDao.updateNumber(lawyerCircle);
    }

    /***
     * 查询律师列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param watchVideos
     * @param department  律师类型
     * @param search    模糊搜索（可以是：律所、律师类型、律师名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<LawyerCircle> findList(int cityId, int watchVideos, long userId, int department, String search, int province, int city, int district, int page, int count) {

        List<LawyerCircle> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (cityId > -1) {
            list = lawyerCircleDao.findList4(cityId, userId);
        } else {
            if (department >= 0) {//按律师类型
                list = lawyerCircleDao.findList(watchVideos, userId, department, province, city, district);
            } else {
                if (!CommonUtils.checkFull(search)) {
                    list = lawyerCircleDao.findList2(watchVideos, userId, search, province, city, district);
                } else {
                    list = lawyerCircleDao.findList3(watchVideos, userId, province, city, district);
                }
            }
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询列表
     * @param users    用户
     * @return
     */
    public List<LawyerCircle> findUsersList(String[] users) {

        List<LawyerCircle> list = null;
        list = lawyerCircleDao.findUsersList(users);
        return list;
    }

    /***
     * 新建
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addRecord(LawyerCircleRecord lawyerCircle) {
        return lawyerCircleDao.addRecord(lawyerCircle);
    }

    /***
     * 更新
     * @param lawyerCircle
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateRecord(LawyerCircleRecord lawyerCircle) {
        return lawyerCircleDao.updateRecord(lawyerCircle);
    }

    /***
     * 更新删除状态
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delRecord(long id, long userId) {
        return lawyerCircleDao.delRecord(id, userId);
    }

    /***
     * 查询列表
     * @param haveDoctor  有无建议：0全部 1没有
     * @param identity   身份区分：0用户查 1医师查
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<LawyerCircleRecord> findRecordList(long userId, int haveDoctor, int identity, int page, int count) {
        List<LawyerCircleRecord> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = lawyerCircleDao.findRecordList(userId, haveDoctor, identity);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upConsultationStatus(LawyerCircleRecord hourlyWorker) {
        return lawyerCircleDao.upConsultationStatus(hourlyWorker);
    }

    /***
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upActualDuration(LawyerCircleRecord hourlyWorker) {
        return lawyerCircleDao.upActualDuration(hourlyWorker);
    }

    /***
     * 查询等待人员列表(默认第一位是正在咨询中，其余为等待中)
     * @param userId   律师ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<LawyerCircleRecord> findWaitList(long userId, int page, int count) {

        List<LawyerCircleRecord> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = lawyerCircleDao.findWaitList(userId);
        return PageUtils.getPageBean(p, list);
    }
}
