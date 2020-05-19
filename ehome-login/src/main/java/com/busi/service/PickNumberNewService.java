package com.busi.service;

import com.busi.dao.PickNumberNewDao;
import com.busi.entity.PickNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 靓号、预售账号、预选账号、VIP账号记录Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class PickNumberNewService {

    @Autowired
    private PickNumberNewDao pickNumberDao;

    /***
     * 新增
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public void add(PickNumber pickNumber){
        pickNumberDao.add(pickNumber);
    }

}
