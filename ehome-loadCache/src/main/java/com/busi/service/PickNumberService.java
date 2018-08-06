package com.busi.service;

import com.busi.dao.PickNumberDao;
import com.busi.entity.PickNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 靓号、预售账号、预选账号、VIP账号记录Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class PickNumberService {

    @Autowired
    private PickNumberDao pickNumberDao;

    /***
     * 更新
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public List<PickNumber> find(){
        List<PickNumber> list = pickNumberDao.find();
        return  list;
    }

}
