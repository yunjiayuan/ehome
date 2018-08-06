package com.busi.service;

import com.busi.entity.HouseNumber;
import com.busi.dao.HouseNumberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 门牌号Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class HouseNumberService {

    @Autowired
    private HouseNumberDao houseNumberDao;

    /***
     * 更新
     * @param houseNumber
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(HouseNumber houseNumber){
        return  houseNumberDao.update(houseNumber);
    }

}
