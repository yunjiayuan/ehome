package com.busi.service;

import com.busi.dao.PurseInfoDao;
import com.busi.entity.Purse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 钱包信息相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class PurseInfoService {

    @Autowired
    private PurseInfoDao purseInfoDao;

    /***
     * 新增
     * @param purse
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addPurseInfo( Purse purse){
        return purseInfoDao.addPurseInfo(purse);
    }

    /***
     * 根据用户ID查询用户钱包信息
     * @param userId
     * @return
     */
    public Purse findPurseInfo(long userId){
        return purseInfoDao.findPurseInfo(userId);
    }

    /***
     * 更新用户钱包信息
     * @param purse
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updatePurseInfo(Purse purse){
        return  purseInfoDao.updatePurseInfo(purse);
    }

}
