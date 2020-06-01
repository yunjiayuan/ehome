package com.busi.servive;

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
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upConsultationStatus(HomeHospitalRecord hourlyWorker) {
        return homeHospitalDao.upConsultationStatus(hourlyWorker);
    }

    /***
     * 查询列表
     * @return
     */
    public List<HomeHospitalRecord> findList() {
        List<HomeHospitalRecord> list = null;
        list = homeHospitalDao.findList();
        return list;
    }

}
