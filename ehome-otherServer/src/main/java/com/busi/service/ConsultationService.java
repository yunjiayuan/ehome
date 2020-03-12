package com.busi.service;

import com.busi.dao.ConsultationDao;
import com.busi.entity.ConsultationFee;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: ehome
 * @description: 律师医生咨询相关
 * @author: ZHaoJiaJie
 * @create: 2020-03-12 16:17:32
 */
@Service
public class ConsultationService {

    @Autowired
    private ConsultationDao consultationDao;

    /***
     * 查询收费信息
     * @param occupation 职业：0医生  1律师
     * @param title 职称
     * @param type     咨询类型：0语音、视频  1图文
     * @return
     */
    public List<ConsultationFee> findList(int occupation, int title, int type) {

        List<ConsultationFee> list = null;
        list = consultationDao.findList(occupation, title, type);

        return list;
    }
}
