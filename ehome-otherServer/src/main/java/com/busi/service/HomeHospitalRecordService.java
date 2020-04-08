package com.busi.service;

import com.busi.dao.HomeHospitalRecordDao;
import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 家医馆咨询
 * @author: ZHaoJiaJie
 * @create: 2020-01-08 10:48
 */
@Service
public class HomeHospitalRecordService {

    @Autowired
    private HomeHospitalRecordDao homeHospitalDao;

    /***
     * 新建
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeHospitalRecord hourlyWorker) {
        return homeHospitalDao.add(hourlyWorker);
    }

    /***
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeHospitalRecord hourlyWorker) {
        return homeHospitalDao.update(hourlyWorker);
    }

    /***
     * 更新支付状态
     * @param hourlyWorkerOrders
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOrders(HomeHospitalRecord hourlyWorkerOrders) {
        return homeHospitalDao.updateOrders(hourlyWorkerOrders);
    }

    /***
     * 更新删除状态
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id, long userId) {
        return homeHospitalDao.del(id, userId);
    }

    /***
     * 查询列表
     * @param haveDoctor  有无医嘱：0全部 1没有
     * @param identity   身份区分：0用户查 1医师查
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<HomeHospitalRecord> findList(long userId, int haveDoctor, int identity, int page, int count) {
        List<HomeHospitalRecord> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeHospitalDao.findList(userId, haveDoctor, identity);
        return PageUtils.getPageBean(p, list);
    }

}
