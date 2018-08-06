package com.busi.service;

import com.busi.dao.ImageDeleteLogDao;
import com.busi.entity.ImageDeleteLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 图片删除记录Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class ImageDeleteLogService {

    @Autowired
    private ImageDeleteLogDao imageDeleteLogDao;

    /***
     * 新增
     * @param imageDeleteLog
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(ImageDeleteLog imageDeleteLog){
        return  imageDeleteLogDao.add(imageDeleteLog);
    }

}
