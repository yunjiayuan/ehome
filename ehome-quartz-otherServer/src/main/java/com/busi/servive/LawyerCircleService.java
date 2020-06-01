package com.busi.servive;

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
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upConsultationStatus(LawyerCircleRecord hourlyWorker) {
        return lawyerCircleDao.upConsultationStatus(hourlyWorker);
    }

    /***
     * 查询列表
     * @return
     */
    public List<LawyerCircleRecord> findList() {
        List<LawyerCircleRecord> list = null;
        list = lawyerCircleDao.findList();
        return list;
    }
}
